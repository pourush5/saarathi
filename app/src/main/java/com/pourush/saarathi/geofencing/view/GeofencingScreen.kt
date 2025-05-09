package com.pourush.saarathi.geofencing.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.pourush.saarathi.geofencing.viewModel.GeofenceViewModel
import kotlin.random.Random

@SuppressLint("MissingPermission")
@Composable
fun GeofencingScreen(geofenceViewModel: GeofenceViewModel, navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val sharedPrefs = context.getSharedPreferences("ChhadiPrefs", Context.MODE_PRIVATE)

    var sliderRadius by remember { mutableStateOf(sharedPrefs.getFloat("radius", 100f)) }
    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var chhadiLocation by remember {
        mutableStateOf(
            sharedPrefs.getString("lat", null)?.toDoubleOrNull()?.let { lat ->
                sharedPrefs.getString("lng", null)?.toDoubleOrNull()?.let { lng ->
                    Pair(lat, lng)
                }
            }
        )
    }
    var chhadiKey by remember { mutableStateOf(sharedPrefs.getString("key", null)) }

    var showManualInput by remember { mutableStateOf(false) }
    var manualLat by remember { mutableStateOf("") }
    var manualLng by remember { mutableStateOf("") }
    var inputKey by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLocation = Pair(it.latitude, it.longitude)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        chhadiLocation?.let { (lat, lng) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Chhadi Location", fontSize = 20.sp, color = Color(0xFF0D47A1))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Lat: $lat\nLng: $lng\nRadius: ${sliderRadius.toInt()} m", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    chhadiKey?.let { key ->
                        Text("Chhadi Key: $key", fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }
        }

        currentLocation?.let { (lat, lng) ->
            Text("Current Location:\nLat: $lat\nLng: $lng", fontSize = 16.sp, color = Color.DarkGray)
        } ?: Text("Fetching current location...", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Radius: ${sliderRadius.toInt()} meters", fontSize = 16.sp)
        Slider(
            value = sliderRadius,
            onValueChange = { sliderRadius = it },
            valueRange = 50f..1000f,
            steps = 9,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                currentLocation?.let { loc ->
                    val key = generateChhadiKey()
                    chhadiLocation = loc
                    chhadiKey = key

                    // Persist values
                    sharedPrefs.edit().apply {
                        putString("lat", loc.first.toString())
                        putString("lng", loc.second.toString())
                        putFloat("radius", sliderRadius)
                        putString("key", key)
                        apply()
                    }

                    geofenceViewModel.addGeofence("chhadi", sliderRadius)
                    Toast.makeText(context, "Chhadi created! Key: $chhadiKey", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Add Chhadi", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showManualInput = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
        ) {
            Text("Join Chhadi", color = Color.White, fontSize = 16.sp)
        }

        if (showManualInput) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = manualLat,
                onValueChange = { manualLat = it },
                label = { Text("Enter Latitude") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = manualLng,
                onValueChange = { manualLng = it },
                label = { Text("Enter Longitude") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = inputKey,
                onValueChange = { inputKey = it },
                label = { Text("Enter Chhadi Key") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val enteredLat = manualLat.toDoubleOrNull()
                    val enteredLng = manualLng.toDoubleOrNull()

                    if (enteredLat != null && enteredLng != null && chhadiLocation != null && inputKey == chhadiKey) {
                        val distance = calculateDistance(
                            chhadiLocation!!.first, chhadiLocation!!.second,
                            enteredLat, enteredLng
                        )

                        if (distance > sliderRadius) {
                            showOutOfRangeNotification(context)
                        } else {
                            Toast.makeText(context, "You are within range! | आप कुटुंब के साथ हैं ", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Invalid coordinates or key!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
            ) {
                Text("Check Range", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

private fun generateChhadiKey(): String {
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    return (1..6).map { chars.random() }.joinToString("")
}

private fun calculateDistance(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val R = 6371000
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return R * c
}

private fun showOutOfRangeNotification(context: Context) {
    val channelId = "geofence_channel"
    val notificationId = 1

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Geofence Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifies when user is out of range"
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("Out of Range!")
        .setContentText("Out of Range | आप छड़ी से दूर हैं। ")
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    with(NotificationManagerCompat.from(context)) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            notify(notificationId, builder.build())
        }
    }
}
