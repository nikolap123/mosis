package com.boopro.btracker.screens.map

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.boopro.btracker.R
import com.boopro.btracker.data.Repository
import com.boopro.btracker.data.model.ComplaintModel
import com.boopro.btracker.data.remote.MapWrapper
import com.boopro.btracker.databinding.FragmentMapBinding
import com.boopro.btracker.helper.Consts
import com.boopro.btracker.helper.Dialogs
import com.boopro.btracker.helper.Permissions
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.MapsInitializer.Renderer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng
    private lateinit var map: GoogleMap
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("OnCreate")
        context?.let {
            MapsInitializer.initialize(it, Renderer.LATEST) {
            }
        }



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        getPermissions()

//        lifecycleScope.launch(Dispatchers.IO) {
//            Repository.getFriendsComplaints().collect {
//                withContext(Dispatchers.Main) {
//                    Consts.friendsComplaints = it
//                }
//            }
//        }
        binding.mapFragmentAddComplaint.setOnClickListener {
            Dialogs.showAddComplaintDialog(requireContext(), currentLocation, map)
        }

        binding.mapFragmentOptions.setOnClickListener {
            Dialogs.showFilterComplaintsDialog(requireContext(), map, currentLocation)
        }

        return binding.root
    }

    private fun setSearchViewListener() {
        binding.mapFragmentSearch.setQuery("", true)

        binding.mapFragmentSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterComplaintsByTitle(newText)
                return false
            }
        })
    }

    private fun filterComplaintsByTitle(newText: String) {
        val filteredComplaints = mutableListOf<ComplaintModel>()

        for (complaint in Consts.currentUserComplaints) {
            if (complaint.title.contains(newText, true) || complaint.content.contains(newText, true)) {
                filteredComplaints.add(complaint)
            }
        }

        for (complaint in Consts.friendsComplaints.values) {
            if (complaint.content.contains(newText, true) || complaint.title.contains(newText, true) || Consts.currentUserFriends[complaint.userId]?.username?.contains(newText, true) == true
            ) {
                filteredComplaints.add(complaint)
            }
        }

        MapWrapper.filterComplaintsByTitle(requireContext(), map, filteredComplaints)
    }

    private fun initializeMap() {
        println("InitMap")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()


        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationProviderClient.getCurrentLocation(102, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
//                currentLocation = LatLng(location.latitude, location.longitude)
                println("LocationSucc " + location)
            }
            .addOnFailureListener { exception ->
                println("Error ")
            }


        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            println("LocationIt" + it)
            if (it != null) {
                currentLocation = LatLng(it.latitude, it.longitude)

                val map =
                    childFragmentManager.findFragmentById(R.id.mapFragmentMap) as SupportMapFragment
                map.getMapAsync { googleMap -> onMapReady(googleMap) }
            }
        }

        setSearchViewListener()
    }

    fun onMapReady(googleMap: GoogleMap) {
        lifecycleScope.launch(Dispatchers.IO){
            Repository.getFriendsComplaints().collect {
                withContext(Dispatchers.Main) {
                    MapWrapper.friendsComplaints(requireContext(), googleMap, it.values.toList())

                    Consts.friendsComplaints = it
                }
            }

        }

        lifecycleScope.launch(Dispatchers.IO){
            Repository.getUserComplaints().collect {
                withContext(Dispatchers.Main) {
                    MapWrapper.userComplaints(requireContext(), googleMap, it)

                    println("UserCompsMap" + it)
                    Consts.currentUserComplaints = it as MutableList<ComplaintModel>;
                }
            }

        }



//        MapWrapper.userComplaints(requireContext(), googleMap)
//
//        MapWrapper.friendsComplaints(requireContext(), googleMap)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        googleMap.isMyLocationEnabled = true

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f))

        googleMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                if (marker.snippet == null) {
                    Dialogs.showUserComplaintMapDialog(requireContext(), marker)
                } else {
                    Dialogs.showFriendComplaintMapDialog(requireContext(), marker, lifecycleScope)
                }
                return false
            }

        })

        map = googleMap
    }


    private fun getPermissions() {
        requestPermissions(Permissions.REQUIRED_MAP_PERMISSIONS, Permissions.REQUEST_CODE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        println("Permisions1")
        if (requestCode == Permissions.REQUEST_CODE_LOCATION) {
            println("Permisions2")
            checkLocation()
        }
    }

    private fun checkLocation() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        println("CheckLocation" + location)
        if (!location) {
            AlertDialog.Builder(requireContext())
                .setTitle(requireContext().getString(R.string.location))
                .setPositiveButton(
                    requireContext().getString(R.string.turn_on),
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Permissions.REQUEST_CODE_LOCATION)
                    }
                )
                .setNegativeButton(
                    requireContext().getString(R.string.cancel),
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        Toast.makeText(requireContext(), requireContext().getText(R.string.GPS), Toast.LENGTH_LONG).show()
                    })
                .setIcon(requireContext().getDrawable(R.drawable.ic_location))
                .show()
        } else {
            initializeMap()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Permissions.REQUEST_CODE_LOCATION) {
            initializeMap()
        }
    }
}