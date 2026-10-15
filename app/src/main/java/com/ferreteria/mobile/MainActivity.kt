package com.ferreteria.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ferreteria.mobile.ui.navegacion.AppNavegacion
import com.ferreteria.mobile.ui.theme.FerreteriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FerreteriaTheme {
                AppNavegacion()
            }
        }
    }
}
