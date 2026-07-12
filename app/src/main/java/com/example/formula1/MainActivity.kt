package com.example.formula1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.formula1.ui.navigation.GraficoNavegacaoApp
import com.example.formula1.ui.theme.TemaFormula1

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as Formula1App
        setContent {
            TemaFormula1 {
                GraficoNavegacaoApp(app = app)
            }
        }
    }
}
