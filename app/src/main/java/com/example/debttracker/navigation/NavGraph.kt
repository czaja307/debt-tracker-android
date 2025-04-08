package com.example.debttracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.debttracker.ui.screens.*
import com.example.debttracker.ui.theme.BottomNavBarColor

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
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
    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(Screen.Home, Screen.NewTransaction, Screen.Friends).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = navController.currentBackStackEntryAsState().value?.destination?.route == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationRoute!!) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
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
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val bottomNavColor = BottomNavBarColor
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = bottomNavColor
    ) {
        listOf(Screen.Home, Screen.NewTransaction, Screen.Friends).forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title, color = Color.White) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationRoute!!) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}