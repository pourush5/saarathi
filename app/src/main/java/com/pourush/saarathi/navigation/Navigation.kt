import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pourush.saarathi.geofencing.view.GeofencingScreen
import com.pourush.saarathi.geofencing.viewModel.GeofenceViewModel
import com.pourush.saarathi.helperScreens.HomeScreen
import com.pourush.saarathi.navigation.Screen

@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    val application = LocalContext.current.applicationContext as Application

    val geofenceViewModel: GeofenceViewModel = viewModel(
        factory = GeofenceViewModelFactory(application)
    )

    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }

        composable(Screen.GeoFenceScreen.route) {
            GeofencingScreen(geofenceViewModel, navController)
        }
    }
}
