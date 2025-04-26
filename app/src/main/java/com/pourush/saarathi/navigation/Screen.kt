package com.pourush.saarathi.navigation

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("home_screen")
    data object GeoFenceScreen : Screen("geo_fence_screen")
}