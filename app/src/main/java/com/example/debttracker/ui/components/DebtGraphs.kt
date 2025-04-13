package com.example.debttracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DebtOverTimeGraph() {
    // placeholder for the debt graph
    // allegedly i heard you can use a library like MPAndroidChart or any other charting library to implement this
    // as for now its a placeholder text only
    Text(text = "Debt Graph Placeholder")
    CustomButton(icon = Icons.Filled.Face, text = "Test Button", onClick = {})
}

@Composable
fun DebtWheelGraph() {
    // placeholder for the debt graph
    // allegedly i heard you can use a library like MPAndroidChart or any other charting library to implement this
    // as for now its a placeholder text only
    Text(text = "Debt Graph Placeholder")
    CustomButton(text = "Test Button", onClick = {})
}