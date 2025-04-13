package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.FriendInvitationField
import com.example.debttracker.ui.theme.AppBackgroundColor
import com.example.debttracker.ui.theme.GlobalTopBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationsScreen(navController: NavHostController) {
    Scaffold(
        containerColor = AppBackgroundColor,
        topBar = {
            BackTopAppBar("Invitations", navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FriendInvitationField(
                friendName = "Alice Johnson",
                username = "@alice_j",
                onAccept = { /* test action */ },
                onReject = { /* test action */ }
            )
            FriendInvitationField(
                friendName = "Bob Brown",
                username = "@bobb",
                onAccept = { /* test action */ },
                onReject = { /* test action */ }
            )
            FriendInvitationField(
                friendName = "Charlie White",
                username = "@charlie_w",
                onAccept = { /* test action */ },
                onReject = { /* test action */ }
            )
        }
    }
}
