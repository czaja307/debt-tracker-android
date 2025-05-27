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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.debttracker.viewmodels.ViewModelFactory
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.debttracker.ui.screens.*
import com.example.debttracker.ui.theme.AccentPrimary
import com.example.debttracker.ui.theme.BottomNavBarColor
import com.example.debttracker.ui.theme.TextPrimary
import com.example.debttracker.viewmodels.LoginViewModel
import androidx.compose.runtime.livedata.observeAsState

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
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(factory = ViewModelFactory(context = context))
    val isLoggedIn by loginViewModel.isLoggedIn.observeAsState(false)
    val startDestination = if (isLoggedIn) Screen.Home.route else "auth"

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(Screen.Home.route, Screen.Friends.route)) {
                CustomBottomNavBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("auth") { AuthHost(navController = navController, loginViewModel = loginViewModel) }
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.NewTransaction.route) { NewTransactionScreen(navController) }
            composable(Screen.Friends.route) { FriendsScreen(navController) }
            composable(Screen.AddFriend.route) { AddFriendScreen(navController) }
            composable(Screen.Invitations.route) { InvitationsScreen(navController) }
            composable(Screen.ProfileSettings.route) {
                ProfileContent(navController)
            }
            composable(
                route = "friend_info/{friendId}",
                arguments = listOf(navArgument("friendId") { type = NavType.StringType })
            ) { backStackEntry ->
                val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
                FriendInfoScreen(navController, friendId)
            }
            
            composable(
                route = "friend_transaction/{friendId}",
                arguments = listOf(navArgument("friendId") { type = NavType.StringType })
            ) { backStackEntry ->
                val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
                FriendTransactionScreen(navController, friendId)
            }
        }
    }
}

@Composable
fun CustomBottomNavBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        containerColor = BottomNavBarColor
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
                            tint = TextPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                label = {
                    if (screen.route != Screen.NewTransaction.route) {
                        Text(text = screen.title, color = TextPrimary, fontSize = 12.sp)
                    }
                }
            )
        }
    }
}