package com.example.weatherforecast.alarm.view

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.example.weatherforecast.R
import com.example.weatherforecast.alarm.view.*
import com.example.weatherforecast.alarm.view.alertviewmodel.ViewModelAlarm
import com.example.weatherforecast.alarm.view.alertviewmodel.ViewModelAlertFactory
import com.example.weatherforecast.databinding.AlertDialogBinding
import com.example.weatherforecast.databinding.FragmentAlertBinding
import com.example.weatherforecast.localsource.LocalSource
import com.example.weatherforecast.localsource.WeatherDataBase
import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.network.AllWeatherRetrofitHelper
import com.example.weatherforecast.network.RemoteSource
import com.example.weatherforecast.repo.Repo
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment() , AlertOnClickListener {

    lateinit var binding : FragmentAlertBinding
    private lateinit var manager : NotificationManager

    private lateinit var builder: AlertDialog.Builder
    private lateinit var bindingDialog: AlertDialogBinding
    private lateinit var dialog: AlertDialog
    private lateinit var mTimePicker: TimePickerDialog
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alerts: List<Alert>

    private var startDate: Long = 0L
    private var endDate: Long = 0L
    private var startTime: Long = 0L
    private var endTime: Long = 0L


    lateinit var  alertViewModel : ViewModelAlarm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        bindingDialog =
            AlertDialogBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        alertViewModel = ViewModelProvider(requireActivity() , ViewModelAlertFactory(
            (Repo.getInstance(RemoteSource(AllWeatherRetrofitHelper.retrofitInstance)
            , LocalSource(WeatherDataBase.getInstance(requireContext())
                    .getDao()))))).get(ViewModelAlarm::class.java)

        lifecycleScope.launch {
            alertViewModel.getAllAlerts().collect {
                alerts = it
                setUpRecyclerView()
                checkEmptyList()
            }
        }

        binding.fabAddAlert.setOnClickListener {
            showAddAlertDialoge()
        }

        bindingDialog.ivClose.setOnClickListener { dialog.dismiss() }

        bindingDialog.tvStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    bindingDialog.tvStartDate.text = "$dayOfMonth/$monthOfYear/$year"
                    startDate = calendar.timeInMillis
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        bindingDialog.tvEndDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    bindingDialog.tvEndDate.text = "$dayOfMonth/$monthOfYear/$year"
                    endDate = calendar.timeInMillis
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        bindingDialog.tvStartTime.setOnClickListener {
            val (hour, minute) = showTimePicker()
            mTimePicker = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    bindingDialog.tvStartTime.text = String.format("%d : %d ", hourOfDay, minute)
                    startTime =
                        (TimeUnit.MINUTES.toSeconds(minute.toLong()) + TimeUnit.HOURS.toSeconds(hour.toLong()))
                }, hour, minute, false
            )
            bindingDialog.tvStartTime.setOnClickListener {
                mTimePicker.show()
            }
        }

        bindingDialog.tvEndTime.setOnClickListener {
            val (hour, minute) = showTimePicker()
            mTimePicker = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    bindingDialog.tvEndTime.text = String.format("%d : %d", hourOfDay, minute)
                    endTime =
                        (TimeUnit.MINUTES.toSeconds(minute.toLong()) + TimeUnit.HOURS.toSeconds(hour.toLong()))

                }, hour, minute, false
            )
            bindingDialog.tvEndTime.setOnClickListener {
                mTimePicker.show()
            }
        }



        bindingDialog.addAlertBtn.setOnClickListener {
            val alert = Alert(
                "",
                bindingDialog.tvStartDate.text.toString(),
                bindingDialog.tvEndDate.text.toString(),
                bindingDialog.tvStartTime.text.toString(),
                bindingDialog.tvEndTime.text.toString()
            )

            lifecycleScope.launch {
                alertViewModel.insertAlert(alert)
                checkEmptyList()
                setScheduler()
                dialog.cancel()
                dialog.dismiss()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setScheduler() {

        val alarmTimeMillis = calculateAlarmTimeMillis()
        val message = "dangerous"

        val notificationIntent = Intent(requireContext(), NotificationReceiver::class.java)
        notificationIntent.putExtra("message", message)

        val pendingIntent = PendingIntent . getBroadcast (
                requireContext(),
        0,
        notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(alarmTimeMillis, pendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                alarmTimeMillis,
                pendingIntent
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAlarmTimeMillis(): Long {
        val selectedStartDate = bindingDialog.tvStartDate.text.toString()
        val selectedEndDate = bindingDialog.tvEndDate.text.toString()
        val selectedStartTime = bindingDialog.tvStartTime.text.toString()
        val selectedEndTime = bindingDialog.tvEndTime.text.toString()

        val startDate = LocalDate.parse(selectedStartDate, DateTimeFormatter.ofPattern("d/M/yyyy"))
        val endDate = LocalDate.parse(selectedEndDate, DateTimeFormatter.ofPattern("d/M/yyyy"))

        val startTime = LocalTime.parse(selectedStartTime.replace(" ", ""), DateTimeFormatter.ofPattern("H:m"))
        val endTime = LocalTime.parse(selectedEndTime.replace(" ", ""), DateTimeFormatter.ofPattern("H:m"))

        val startDateTime = LocalDateTime.of(startDate, startTime)
        val endDateTime = LocalDateTime.of(endDate, endTime)

        val alarmDateTime = startDateTime.minusMinutes(endDateTime.minute.toLong())
            .minusHours(endDateTime.hour.toLong())
            .minusDays(endDateTime.dayOfMonth.toLong())

        return alarmDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
    }


    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvAlerts.layoutManager = layoutManager
        alertAdapter = AlertAdapter(alerts, requireContext(), this)
        binding.rvAlerts.adapter = alertAdapter
    }

    private fun showTimePicker(): Pair<Int, Int> {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        return Pair(hour, minute)
    }

    private fun showAddAlertDialoge() {
        builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)

        val parent = bindingDialog.root.parent as? ViewGroup
        parent?.removeView(bindingDialog.root)

        builder.setView(bindingDialog.root)
        dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
    }

    override  fun onOptionClicked(alert: Alert) {

        lifecycleScope.launch {
            alertViewModel.deleteAlert(alert)
        }
        showSnackBar(alert)
        checkEmptyList()
        alertAdapter.notifyDataSetChanged()
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(alert.id.toString())
    }

    private fun checkEmptyList() {
        if (alerts.isEmpty()) {
            binding.ivEmpty.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvAlerts.visibility = View.GONE
        } else {
            binding.ivEmpty.visibility = View.GONE
            binding.rvAlerts.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    private fun showSnackBar(alert: Alert) {
        val snackbar = Snackbar.make(binding.layoutAlert, "Removed from Fav!", Snackbar.LENGTH_LONG)
        snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
        snackbar.setAction("undo") {
            lifecycleScope.launch {
            alertViewModel.insertAlert(alert)
            Toast.makeText(requireContext(), "added again!", Toast.LENGTH_SHORT).show()}
        }.show()
    }

}


