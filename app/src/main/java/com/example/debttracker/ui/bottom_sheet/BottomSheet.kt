package com.example.debttracker.ui.bottom_sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class BottomSheet {
    @Composable
    fun BottomSheetScreen() {
        MainBottomSheet()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomSheet() {
    BottomSheetScaffold(
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3000.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Bottom sheet content", fontSize = 30.sp)
            }
        },
        sheetContainerColor = Color(0xFFA8C931),
        sheetPeekHeight = (LocalConfiguration.current.screenHeightDp * 0.4).dp,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "HERE IS THE MAIN SCREEN", fontSize = 30.sp)
        }
    }
}