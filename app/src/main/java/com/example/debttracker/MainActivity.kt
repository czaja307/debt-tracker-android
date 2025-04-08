package com.example.debttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.debttracker.navigation.NavGraph
import com.example.debttracker.ui.theme.DebtTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DebtTrackerTheme {
                NavGraph()
            }
        }
    }
}
