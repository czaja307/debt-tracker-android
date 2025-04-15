package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.GlobalTopAppBar

@Composable
fun FriendInfoScreen(navController: NavHostController, friendId: String) {
    Scaffold (
        topBar = { BackTopAppBar("friend info screen", navController) },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = "Friend Info Screen for friend: $friendId",
                    fontSize = 20.sp,
                )
            }
        }
    )
}