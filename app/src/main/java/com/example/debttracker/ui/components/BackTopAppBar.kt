package com.example.debttracker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.R
import com.example.debttracker.ui.theme.GlobalTopBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopAppBar(title: String, navController: NavHostController) {
    TopAppBar(
        title = {
            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("top_app_bar_title")
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalTopBarColor)
    )
}
