package com.pourush.saarathi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.pourush.saarathi.geofencing.service.GeofenceService
import com.pourush.saarathi.geofencing.view.GeofencingScreen
import com.pourush.saarathi.geofencing.viewModel.GeofenceViewModel

class MainActivity : ComponentActivity() {

    private lateinit var geofenceViewModel: GeofenceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Start the GeofenceService
        startService(Intent(this, GeofenceService::class.java))

        // Initialize ViewModel with the service
        geofenceViewModel = GeofenceViewModel(GeofenceService(), this)

        setContent {
            GeofencingScreen(geofenceViewModel)
        }
    }
}