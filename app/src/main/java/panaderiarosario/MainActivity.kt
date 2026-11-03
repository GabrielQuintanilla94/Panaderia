package panaderiarosario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import panaderiarosario.ui.PanaderiaApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (panaderiarosario.BuildConfig.FIREBASE_EMULATORS) {
            // Emulator-only identifiers; these never point to a real Firebase project.
            com.google.firebase.FirebaseApp.initializeApp(this,
                com.google.firebase.FirebaseOptions.Builder()
                    .setProjectId("demo-panaderia")
                    .setApplicationId("1:1234567890:android:0000000000000000")
                    .setApiKey("demo-emulator-api-key")
                    .build())
        } else {
            com.google.firebase.FirebaseApp.initializeApp(this)
        }
        val appState = androidx.lifecycle.ViewModelProvider(this)[AppState::class.java]
        setContent {
            PanaderiaApp(appState)
        }
    }
}
