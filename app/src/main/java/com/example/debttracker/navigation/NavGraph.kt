package com.example.debttracker.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.debttracker.ui.screens.*
import com.example.debttracker.ui.theme.AccentPrimary

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object NewTransaction : Screen("new_transaction", "New Transaction", Icons.Filled.Add)
    object Friends : Screen("friends", "Friends", Icons.Filled.Person)
    object AddFriend : Screen("add_friend", "Add Friend", Icons.Filled.Add)
    object Invitations : Screen("invitations", "Invitations", Icons.Filled.Person)
    object ProfileSettings : Screen("profile_settings", "Profile Settings", Icons.Filled.Person)
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(Screen.Home.route, Screen.Friends.route)) {
                CustomBottomNavBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.NewTransaction.route) { NewTransactionScreen(navController) }
            composable(Screen.Friends.route) { FriendsScreen(navController) }
            composable(Screen.AddFriend.route) { AddFriendScreen(navController) }
            composable(Screen.Invitations.route) { InvitationsScreen(navController) }
            composable(Screen.ProfileSettings.route) { ProfileSettingsScreen(navController) }
            composable(
                route = "friend_info/{friendId}",
                arguments = listOf(navArgument("friendId") { type = NavType.StringType })
            ) { backStackEntry ->
                val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
                FriendInfoScreen(navController, friendId)
            }
        }
    }
}

@Composable
fun CustomBottomNavBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        containerColor = Color.Black.copy(alpha = 0.5f)
    ) {
        listOf(Screen.Home, Screen.NewTransaction, Screen.Friends).forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(
                            navController.graph.startDestinationRoute ?: Screen.Home.route
                        ) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    if (screen.route == Screen.NewTransaction.route) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                //.offset(y = (-20).dp)
                                .clip(CircleShape)
                                .background(AccentPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = Color.Black,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                label = {
                    if (screen.route != Screen.NewTransaction.route) {
                        Text(text = screen.title, color = Color.White, fontSize = 12.sp)
                    }
                }
            )
        }
    }
}
