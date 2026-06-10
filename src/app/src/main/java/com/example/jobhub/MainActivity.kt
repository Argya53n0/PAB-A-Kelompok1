package com.example.jobhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.jobhub.ui.screens.LoginScreen
import com.example.jobhub.ui.screens.RegisterScreen
import com.example.jobhub.ui.theme.JOBHUBTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JOBHUBTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    JobHubApp()
                }
            }
        }
    }
}

@Composable
fun JobHubApp() {
    // Simple state-based navigation for presentation mockup
    var currentScreen by remember { mutableStateOf("Login") }

    when (currentScreen) {
        "Login" -> LoginScreen(
            onNavigateToRegister = { currentScreen = "Register" }
        )
        "Register" -> RegisterScreen(
            onNavigateToLogin = { currentScreen = "Login" }
        )
    }
}