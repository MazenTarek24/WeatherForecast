package com.example.weatherforecast.alarm.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.ItemAlertBinding
import com.example.weatherforecast.model.onecall.Alert

class AlertAdapter(
    val alerts: List<Alert>,
    val context: Context,
    private val alertOnClickListener: AlertOnClickListener
) :
    RecyclerView.Adapter<AlertAdapter.AlertsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsViewHolder {

        return (AlertsViewHolder(
            ItemAlertBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ))
    }

    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        val alerts = alerts[position]
        holder.binding.tvItemStartTime.text = alerts.startTime.toString()
        holder.binding.tvEndTime.text = alerts.endTime.toString()
        holder.binding.tvStartDate.text = alerts.startDate.toString()
        holder.binding.tvEndDate.text=alerts.endDate.toString()
        holder.binding.ivOption.setOnClickListener {
            alertOnClickListener.onOptionClicked(alerts)
        }
    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    //======================================================
    class AlertsViewHolder(val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}
