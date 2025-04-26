package com.pourush.saarathi.geofencing.service

import android.Manifest
import android.app.Service
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.pourush.saarathi.geofencing.model.GeofenceModel

class GeofenceService : Service() {

    private lateinit var geofencingClient: GeofencingClient

    override fun onCreate() {
        super.onCreate()
        // Use the applicationContext to initialize the GeofencingClient
        geofencingClient = LocationServices.getGeofencingClient(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder? = null


    private fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun addGeofence(geofenceModel: GeofenceModel) {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("GeofenceService", "Location permission not granted")
            return
        }

        // Building the geofence
        val geofence = Geofence.Builder()
            .setRequestId(geofenceModel.id)
            .setCircularRegion(
                geofenceModel.latitude,
                geofenceModel.longitude,
                geofenceModel.radius
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        // Build the geofencing request
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        // Add the geofence
        geofencingClient.addGeofences(request, getGeofencePendingIntent())
            .addOnSuccessListener {
                Log.d("GeofenceService", "Geofence added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("GeofenceService", "Failed to add geofence: ${e.message}")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("GeofenceService", "Service Destroyed")
    }
}