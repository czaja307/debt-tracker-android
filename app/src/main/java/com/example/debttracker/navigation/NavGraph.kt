package com.example.debttracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.debttracker.ui.screens.Screen1
import com.example.debttracker.ui.screens.Screen2
import com.example.debttracker.ui.screens.Screen3

sealed class Screen(val route: String, val title: String) {
    object Screen1 : Screen("screen1", "Ekran 1")
    object Screen2 : Screen("screen2", "Ekran 2")
    object Screen3 : Screen("screen3", "Ekran 3")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Screen1.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Screen1.route) { Screen1() }
            composable(Screen.Screen2.route) { Screen2() }
            composable(Screen.Screen3.route) { Screen3() }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        listOf(
            Screen.Screen1,
            Screen.Screen2,
            Screen.Screen3
        ).forEach { screen ->
            val icon = when (screen) {
                Screen.Screen1 -> Icons.Filled.Home
                Screen.Screen2 -> Icons.Filled.Favorite
                Screen.Screen3 -> Icons.Filled.Person
            }
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // podobno zapobiega powstawaniu duplikatów w back stack
                            popUpTo(navController.graph.startDestinationRoute!!) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
