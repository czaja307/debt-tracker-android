package com.example.debttracker.ui.screens

//import com.example.debttracker.models.User
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.debttracker.R
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

//public static example user list
val exampleUserList = listOf(
    User(id = "1", name = "John Doe", balance = 25.0f, imageRes = null),
    User(id = "2", name = "Jane Doe", balance = -15.5f, imageRes = R.drawable.profile_pic),
    User(id = "3", name = "Alice Smith", balance = 12.3f, imageRes = R.drawable.sheldon),
    User(id = "4", name = "Bob Johnson", balance = 55.0f, imageRes = R.drawable.app_logo)
)

@Composable
fun FriendsScreen(
    navController: NavHostController,
    friendList: List<User> = exampleUserList
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
