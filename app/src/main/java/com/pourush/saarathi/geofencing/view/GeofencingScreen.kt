package com.pourush.saarathi.geofencing.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pourush.saarathi.geofencing.viewModel.GeofenceViewModel

@Composable
fun GeofencingScreen(geofenceViewModel: GeofenceViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { geofenceViewModel.addGeofence("geo_id", 100f) }) {
            Text(text = "Set Geofence at Current Location")
        }
    }
}