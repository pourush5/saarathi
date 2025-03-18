package com.pourush.saarathi.geofencing.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.pourush.saarathi.geofencing.model.GeofenceModel
import com.pourush.saarathi.geofencing.service.GeofenceService

class GeofenceViewModel(private val geofenceService: GeofenceService, context: Context) : ViewModel() {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun addGeofence(id: String, radius: Float) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geofenceModel = GeofenceModel(id, location.latitude, location.longitude, radius)
                geofenceService.addGeofence(geofenceModel)
            }
        }
    }
}