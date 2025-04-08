package com.example.debttracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.R
import com.example.debttracker.ui.theme.GlobalTopBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalTopAppBar(navController: NavHostController) {
    val topAppBarColor = GlobalTopBarColor
    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(40.dp)
            )
        },
        actions = {
            IconButton(onClick = { navController.navigate("profile_settings") }) {
                Image(
                    painter = painterResource(id = R.drawable.profile_pic),
                    contentDescription = "Profile",
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = topAppBarColor)
    )
}
