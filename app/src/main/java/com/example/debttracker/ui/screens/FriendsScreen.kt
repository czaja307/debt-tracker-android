package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.debttracker.ui.components.FriendField
import com.example.debttracker.ui.components.FriendInvitationField

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.FriendField

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.example.debttracker.ui.components.FriendField
import com.example.debttracker.ui.components.GlobalTopAppBar

@Composable
fun FriendsScreen(navController: NavHostController) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { navController.navigate("add_friend") }) {
                        Text("Add Friend")
                    }
                    Button(onClick = { navController.navigate("invitations") }) {
                        Text("Invitations")
                    }
                }
                FriendField(friendName = "John Doe", balance = 25.0f)
                FriendField(friendName = "John Doe", balance = -25.0f)
                FriendField(friendName = "John Doe", balance = 12.30f)
                FriendField(friendName = "John Doe", balance = 55.0f)
            }
        }
    )
}
