package com.pourush.saarathi.helperScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pourush.saarathi.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saarathi - Traveler Assistance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE) // Purple color for the top bar
                )
            )
        },
        containerColor = Color(0xFFF5F5F5) // Light gray background for the screen
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp), // Increased padding for better spacing
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp) // Increased spacing between buttons
            ) {
                FeatureButton(
                    text = "Geofencing Assistance",
                    gradientColors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF)), // Blue gradient
                    onClick = { navController.navigate(Screen.GeoFenceScreen.route) }
                )
                FeatureButton(
                    text = "Itinerary Planner",
                    gradientColors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC)), // Purple gradient
                    onClick = { /* Handle Itinerary Planner Click */ }
                )
                FeatureButton(
                    text = "Community & Safety Tips",
                    gradientColors = listOf(Color(0xFFF857A6), Color(0xFFFF5858)), // Pink gradient
                    onClick = { /* Handle Community & Safety Tips Click */ }
                )
            }
        }
    }
}

@Composable
fun FeatureButton(
    text: String,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Increased button height for better visibility
            .background(
                brush = Brush.horizontalGradient(gradientColors), // Gradient background
                shape = RoundedCornerShape(16.dp) // Rounded corners
            ),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Transparent to show gradient
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp) // Subtle shadow
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold // Bold text for better readability
        )
    }
}