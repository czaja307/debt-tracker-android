package com.example.debttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.debttracker.viewmodels.LoginViewModel
import androidx.compose.ui.graphics.Color
import com.example.debttracker.ui.components.CustomText

@Composable
fun AddFriendScreen(navController: NavHostController, loginViewModel: LoginViewModel = viewModel()) {
    // Use ViewModel LiveData for friend email input
    val friendEmail by loginViewModel.friendEmail.observeAsState("")
    val hasError by loginViewModel.hasError.observeAsState(false)
    val errorMessage by loginViewModel.errorMessage.observeAsState("")

    Scaffold(
        modifier = Modifier.background(AppBackgroundColor),
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
                value = friendEmail,
                onValueChange = { loginViewModel.friendEmail.value = it },
                label = "Friend Email",
                placeholder = "Enter friend email",
                modifier = Modifier.fillMaxWidth()
            )
            if (hasError) {
                Spacer(modifier = Modifier.height(8.dp))
                CustomText(text = errorMessage, color = Color.Red)
            }
            CustomButton(
                text = "Send Invite",
                onClick = {
                    loginViewModel.sendFriendRequest(friendEmail)
                    navController.navigateUp()
                },
            )
        }
    }
}
