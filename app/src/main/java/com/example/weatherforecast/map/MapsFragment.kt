package com.example.weatherforecast.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.Coordinates
import com.example.weatherforecast.R
import com.example.weatherforecast.setting.viewmodel.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsFragment : Fragment() , OnMapReadyCallback , GoogleMap.OnMarkerClickListener{


    private lateinit var mMap : GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val locationViewModel : LocationViewModel by activityViewModels()

    private var isFromFavFragment = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        isFromFavFragment  = arguments?.getBoolean("fromFavFragment")?: false

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener { latlng->
            mMap.clear()
            placeMarkerOnMap(latlng)

        }
        setUpMap()

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location->
            if (location != null)
            {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude , location.longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,12f))
            }

        }

    }

    private fun placeMarkerOnMap(currentLatLng: LatLng) {
        mMap.clear()

        val markerOption = MarkerOptions().position(currentLatLng)
        markerOption.title("$currentLatLng")
        mMap.addMarker(markerOption)


    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val lat = marker.position.latitude
        val lon = marker.position.longitude
        val address = getAddressFromLatLng(requireContext(), lat, lon)
        val locationName = address?.locality ?: ""
        val coordinates = Coordinates(lat, lon , locationName)

        if (isFromFavFragment) {
            val navController = findNavController()
            val bundle = bundleOf("lat" to lat, "lon" to lon)
            navController.previousBackStackEntry?.savedStateHandle?.set("selectedLocation", bundle)
            navController.popBackStack()
        } else {

            /// pass obiecte cordinate to details fragment

            locationViewModel.selectedLocationwithName(lat, lon, locationName)
            val action = MapsFragmentDirections.actionMapsFragmentToDetailsFragment(
                    coordinates)
            findNavController().navigate(action)
        }

        return true
    }

    private fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        if (addresses != null && addresses.isNotEmpty()) {
            return addresses[0]
        }
        return null
    }
}