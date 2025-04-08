package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.CustomEnumPickField
import com.example.debttracker.ui.components.BalanceField
import com.example.debttracker.ui.components.FriendInvitationField
import com.example.debttracker.ui.components.GlobalTopAppBar

@Composable
fun NewTransactionScreen(navController: NavHostController) {
    Scaffold (
        topBar = { GlobalTopAppBar(navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "New Transaction Content", color = androidx.compose.ui.graphics.Color.White)
                var selectedOption by remember { mutableStateOf("Option 1") }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomEnumPickField(
                        label = "Select Option",
                        options = listOf("Option 1", "Option 2", "Option 3"),
                        selectedOption = selectedOption,
                        onOptionSelected = { selectedOption = it }
                    )
                    BalanceField(balance = 123.45f)
                    FriendInvitationField(
                        friendName = "Jane Smith",
                        username = "@jane_s",
                        onAccept = { /* test action */ },
                        onReject = { /* test action */ }
                    )
                }
            }
        }
    )
}


