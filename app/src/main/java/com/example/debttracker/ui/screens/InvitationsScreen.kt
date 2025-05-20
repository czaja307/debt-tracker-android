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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.debttracker.viewmodels.LoginViewModel
import com.example.debttracker.models.FirestoreUser

@Composable
fun InvitationsScreen(navController: NavHostController, loginViewModel: LoginViewModel = viewModel()) {
    Scaffold(
        containerColor = AppBackgroundColor,
        topBar = {
            BackTopAppBar("Invitations", navController)
        }
    ) { innerPadding ->
        val storedUser by loginViewModel.storedUser.observeAsState()
        val incoming = storedUser?.incomingRequests.orEmpty()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            incoming.forEach { friendId ->
                FriendInvitationField(
                    friendName = friendId,
                    username = friendId,
                    onAccept = { loginViewModel.acceptFriendRequest(friendId) },
                    onReject = { /* optional: handle rejection */ }
                )
            }
        }
    }
}
