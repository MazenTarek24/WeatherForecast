package com.example.weatherforecast.favourite

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.Coordinates
import com.example.weatherforecast.R
import com.example.weatherforecast.setting.viewmodel.LocationViewModel
import com.example.weatherforecast.databinding.FragmentFavBinding
import com.example.weatherforecast.localsource.FavoriteLocationEntity
import com.example.weatherforecast.localsource.WeatherDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class FavFragment : Fragment()  , OnItemClick {

    private lateinit var binding : FragmentFavBinding

    private lateinit var listFav: ArrayList<FavoriteLocationEntity>
    private lateinit var adapterFav: RvFavAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentFavBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listFav = ArrayList()
        adapterFav = RvFavAdapter(listFav, this)
        binding.recyclerFav.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFav.adapter = adapterFav

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or
                ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deleteItem(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.recyclerFav)


        val favDao = WeatherDataBase.getInstance(requireContext()).getDao()
        favDao.getAllFav().observe(viewLifecycleOwner) { loc ->
            listFav.clear()
            listFav.addAll(loc)
            adapterFav.notifyDataSetChanged()
        }
        val selectedLocationBundle =
            findNavController().currentBackStackEntry?.savedStateHandle?.get<Bundle>("selectedLocation")
        if (selectedLocationBundle != null) {
            val lat = selectedLocationBundle.getDouble("lat")
            val lon = selectedLocationBundle.getDouble("lon")

            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geoCoder.getFromLocation(lat, lon, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val locationName = address.locality
                        ?: address.adminArea
                        ?: address.countryName
                        ?: "Un Known"

                    val favLocation = FavoriteLocationEntity(lat = lat, lon = lon, name = locationName)
                    CoroutineScope(Dispatchers.IO).launch {
                        favDao.insertLocation(favLocation)
                    }
                }
            }

            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Bundle>("selectedLocation")

          }

        binding.fabAddPlace.setOnClickListener {
            val bundel = bundleOf("fromFavFragment" to true)
            findNavController().navigate(R.id.action_favFragment_to_mapsFragment, bundel)
        }
    }


    private fun deleteItem(position: Int) {
        val deleteLoc = listFav[position]
        val favLDao = WeatherDataBase.getInstance(requireContext()).getDao()
        CoroutineScope(Dispatchers.IO).launch {
            favLDao.deleteFav(deleteLoc)
        }

    }

    override fun onItemClick(locationEntity: FavoriteLocationEntity) {
        val lat = locationEntity.lat
        val lon = locationEntity.lon
        val locationName = locationEntity.name
        val cordinate = Coordinates(lat,lon,locationName)

        val action = FavFragmentDirections.actionFavFragmentToDetailsFragment(
                cordinate)
        findNavController().navigate(action)
    }
}
