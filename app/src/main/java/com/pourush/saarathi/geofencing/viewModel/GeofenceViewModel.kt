package com.pourush.saarathi.geofencing.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.Toast
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

    private var currentChhadiKey: String = ""

    fun addGeofence(id: String, radius: Float) {
        chhadiRadius = radius
        currentChhadiKey = generateChhadiKey()
        Log.d("GeofenceViewModel", "Chhadi stored with radius: $radius")
        Toast.makeText(context, "Chhadi stored with radius: $radius", Toast.LENGTH_SHORT).show()
    }

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
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Toast.makeText(context, "Geofence added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add geofence: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("Geofence", "Error: ${it.message}")
            }
    }

    private fun generateChhadiKey(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
