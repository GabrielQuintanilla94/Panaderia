plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.android.application") version "8.7.3" apply false
    kotlin("android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}

// Keep generated Android files out of OneDrive and long checkout paths on Windows.
if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
    val localAppData = System.getenv("LOCALAPPDATA")
    if (!localAppData.isNullOrBlank()) {
        val checkoutId = rootDir.canonicalPath.hashCode().toUInt().toString(16)
        val outputRoot = file("$localAppData/PanaderiaBuild/$checkoutId")
        allprojects {
            val module = if (path == ":") "root" else path.trim(':').replace(':', '/')
            layout.buildDirectory.set(outputRoot.resolve(module))
        }
    }
}
