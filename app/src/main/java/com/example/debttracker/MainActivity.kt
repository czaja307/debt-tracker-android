package com.example.debttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.debttracker.ui.theme.DebtTrackerTheme
import com.example.debttracker.ui.bottom_sheet.BottomSheet

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DebtTrackerTheme {
                BottomSheet().BottomSheetScreen()
            }
        }
    }
}
