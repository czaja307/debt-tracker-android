package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.FriendInvitationField
import com.example.debttracker.ui.theme.AppBackgroundColor

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
