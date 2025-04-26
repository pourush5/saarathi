package com.pourush.saarathi.geofencing.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.*
import com.pourush.saarathi.geofencing.service.GeofenceBroadcastReceiver
import android.app.PendingIntent
import android.content.Intent
import android.Manifest
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class GeofenceViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(application)

    private val settingsClient: SettingsClient =
        LocationServices.getSettingsClient(application)

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(getApplication(), GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            getApplication(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(id: String, radius: Float) {
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("GeofenceViewModel", "Permission not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geofence = Geofence.Builder()
                    .setRequestId(id)
                    .setCircularRegion(
                        location.latitude,
                        location.longitude,
                        radius
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()

                val geofencingRequest = GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build()

                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()

                val locationSettingsRequest = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .build()

                settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener {
                        // Device location settings are satisfied, so add the geofence
                        geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                            .addOnSuccessListener {
                                Log.d("GeofenceViewModel", "Geofence added successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("GeofenceViewModel", "Failed to add geofence: ${e.message}")
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("GeofenceViewModel", "Location settings are inadequate: ${e.message}")
                    }
            } else {
                Log.e("GeofenceViewModel", "Location is null")
            }
        }.addOnFailureListener {
            Log.e("GeofenceViewModel", "Failed to get last location: ${it.message}")
        }
    }
}
