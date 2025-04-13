package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomTextField
import com.example.debttracker.ui.theme.AppBackgroundColor
import com.example.debttracker.ui.theme.GlobalTopBarColor

@OptIn(ExperimentalMaterial3Api::class)
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
