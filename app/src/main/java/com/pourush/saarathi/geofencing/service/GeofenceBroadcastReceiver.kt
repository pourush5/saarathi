package com.pourush.saarathi.geofencing.service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                Toast.makeText(context, "Geofencing Error", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition
        val triggeringGeofences = geofencingEvent?.triggeringGeofences

        if (triggeringGeofences != null) {
            for (geofence in triggeringGeofences) {
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Toast.makeText(context, "Entered ${geofence.requestId} area", Toast.LENGTH_LONG).show()
                    }
                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Toast.makeText(context, "Exited ${geofence.requestId} area", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
