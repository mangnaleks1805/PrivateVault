package com.privatevault.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application entry point.
 *
 * @HiltAndroidApp triggers Hilt's code generation and sets up
 * the application-level dependency injection component.
 */
@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set up logging — only plant a debug tree in debug builds.
        // Release builds get NO logging tree → logs are silently dropped.
        // NEVER log encryption keys, file paths, or media content.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Create a .nomedia file in filesDir so no media scanner
        // ever indexes our private storage directory.
        runCatching {
            java.io.File(filesDir, ".nomedia").createNewFile()
        }

        Timber.d("App started — private vault initialized")
    }
}
