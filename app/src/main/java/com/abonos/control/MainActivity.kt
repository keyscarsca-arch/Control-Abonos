package com.abonos.control

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.abonos.control.ui.navigation.GrafoNavegacion
import com.abonos.control.ui.theme.ControlAbonosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControlAbonosTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GrafoNavegacion()
                }
            }
        }
    }
}
