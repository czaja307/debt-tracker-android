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
import androidx.compose.ui.unit.sp
//import com.example.debttracker.models.User
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.FriendField
import com.example.debttracker.ui.components.GlobalTopAppBar

// temporary, kiedyś sobie zamienie na models.User,
// ale nie wiem czy ta klasa nie ulegnie jeszcze zmienom różnym
data class User(
    val id: String,
    val name: String,
    val balance: Float,
    val imageRes: Int? = null
)

@Composable
fun FriendsScreen(
    navController: NavHostController,
    friendList: List<User> = listOf(
        User(id = "1", name = "John Doe", balance = 25.0f),
        User(id = "2", name = "Jane Doe", balance = -15.5f),
        User(id = "3", name = "Alice Smith", balance = 12.3f),
        User(id = "4", name = "Bob Johnson", balance = 55.0f)
    )
) {
    Scaffold(
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
                    CustomButton(
                        text = "Add Friend",
                        onClick = { navController.navigate("add_friend") },
                        aspectRatio = 4f,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    CustomButton(
                        text = "Invitations",
                        onClick = { navController.navigate("invitations") },
                        aspectRatio = 4f,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (friendList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomText(
                            text = "You have no friends added",
                            fontSize = 20.sp,
                        )
                    }
                } else {
                    friendList.forEach { friend ->
                        FriendField(
                            friend = friend,
                            navController = navController
                        )
                    }
                }
            }
        }
    )
}
