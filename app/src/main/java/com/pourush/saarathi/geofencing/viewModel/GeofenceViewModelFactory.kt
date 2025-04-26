import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pourush.saarathi.geofencing.viewModel.GeofenceViewModel

class GeofenceViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeofenceViewModel::class.java)) {
            return GeofenceViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
