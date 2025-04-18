package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.ButtonVariant
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomEnumPickField
import com.example.debttracker.ui.components.CustomNumberField

@Composable
fun NewTransactionScreen(navController: NavHostController) {
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var payer by remember { mutableStateOf("") }
    var debtor by remember { mutableStateOf("") }

    Scaffold(
        topBar = { BackTopAppBar("Add a new transaction", navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomNumberField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Enter an amount",
                    placeholder = "$0.00",
                    modifier = Modifier.fillMaxWidth()
                )

                CustomEnumPickField(
                    label = "Choose Currency",
                    options = listOf("USD", "EUR", "PLN"),
                    selectedOption = currency,
                    onOptionSelected = { currency = it },
                    modifier = Modifier.fillMaxWidth()
                )

                CustomEnumPickField(
                    label = "Who was paying?",
                    options = listOf("You", "Friend 1", "Friend 2"),
                    selectedOption = payer,
                    onOptionSelected = { payer = it },
                    modifier = Modifier.fillMaxWidth()
                )

                CustomEnumPickField(
                    label = "Who was in debt?",
                    options = listOf("You", "Friend 1", "Friend 2"),
                    selectedOption = debtor,
                    onOptionSelected = { debtor = it },
                    modifier = Modifier.fillMaxWidth()
                )

                CustomButton(
                    variant = ButtonVariant.LIME,
                    icon = Icons.Default.Add,
                    text = "Add",
                    onClick = {
                        // testowa akcja typu dodanie transakcji
                        navController.navigateUp()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}


