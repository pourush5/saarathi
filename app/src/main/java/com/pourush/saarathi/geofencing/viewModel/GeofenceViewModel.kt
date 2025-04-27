package com.pourush.saarathi.geofencing.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.*
import com.pourush.saarathi.geofencing.service.GeofenceBroadcastReceiver

class GeofenceViewModel(application: Application) : AndroidViewModel(application) {

    private val geofencingClient = LocationServices.getGeofencingClient(application)
    private val settingsClient = LocationServices.getSettingsClient(application)
    private val context = getApplication<Application>()

    private var chhadiLat: Double? = null
    private var chhadiLng: Double? = null
    private var chhadiRadius: Float = 100f

    // Save Chhadi location
    fun addGeofence(id: String, radius: Float) {
        chhadiRadius = radius
        Log.d("GeofenceViewModel", "Chhadi stored with radius: $radius")
        Toast.makeText(context,"Chhadi stored with radius: $radius",Toast.LENGTH_SHORT).show()
    }

    // Actually Join (Create Geofence)
    @SuppressLint("MissingPermission")
    fun addManualGeofence(
        id: String,
        centerLat: Double,
        centerLng: Double,
        radius: Float,
        onExit: () -> Unit
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(centerLat, centerLng, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .addGeofence(geofence)
            .build()

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()

        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                    .addOnSuccessListener {
                        Log.d("GeofenceViewModel", "Geofence added successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("GeofenceViewModel", "Failed to add geofence: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("GeofenceViewModel", "Location settings error: ${e.message}")
            }
    }

    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
