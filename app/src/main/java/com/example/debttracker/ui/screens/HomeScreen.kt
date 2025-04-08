package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.TransactionField
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomTextField
import com.example.debttracker.ui.components.GlobalTopAppBar

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = { GlobalTopAppBar(navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Home Screen Content", color = androidx.compose.ui.graphics.Color.White)

                var textValue by remember { mutableStateOf("") }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TransactionField(date = "2025-04-08", amount = "$50.00")
                    CustomButton(icon = Icons.Filled.Info, text = "Test Button")
                    CustomTextField(label = "Description", text = textValue, onTextChange = { textValue = it })
                }
            }
        }
    )
}


