package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomTextField
import com.example.debttracker.ui.theme.AppBackgroundColor

@Composable
fun AddFriendScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }

    Scaffold(
        containerColor = AppBackgroundColor,
        topBar = { BackTopAppBar("Add Friend", navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = username,
                onValueChange = { username = it },
                label = "Enter username",
                placeholder = "username here",
                modifier = Modifier.fillMaxWidth()
            )
            CustomButton(
                text = "Send Invite",
                onClick = {
                    // testowa akcja typu wysłanie zaproszenia do znaj.
                    navController.navigateUp()
                },
            )
        }
    }
}
