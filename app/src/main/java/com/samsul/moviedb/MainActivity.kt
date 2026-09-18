package com.samsul.moviedb

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.samsul.moviedb.presentation.navigation.AppNavGraph
import com.samsul.moviedb.ui.theme.CinemaBackground
import com.samsul.moviedb.ui.theme.TechnicalTestAndroidTheme

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val overrideConfig = Configuration(newBase.resources.configuration).apply {
            fontScale = 1.0f
        }
        val context = newBase.createConfigurationContext(overrideConfig)
        super.attachBaseContext(context)
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        overrideConfiguration?.fontScale = 1.0f
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechnicalTestAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CinemaBackground
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}