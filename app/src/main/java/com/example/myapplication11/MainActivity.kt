package com.example.myapplication11

import ProductViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplication11.ui.theme.MyApplication11Theme
import com.example.myapplication11.view.AppNavigation

class MainActivity : ComponentActivity() {
    // Initialisation de ProductViewModel
    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplication11Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Passez le ViewModel à AppNavigation si nécessaire
                    AppNavigation()
                }
            }
        }
    }
}
