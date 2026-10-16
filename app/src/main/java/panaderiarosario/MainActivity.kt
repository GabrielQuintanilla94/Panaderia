package panaderiarosario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import panaderiarosario.ui.PanaderiaApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appState = AppState()
        setContent {
            PanaderiaApp(appState)
        }
    }
}
