package com.example.debttracker.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.debttracker.R
import com.example.debttracker.data.PreferencesManager
import com.example.debttracker.ui.theme.GlobalTopBarColor
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalTopAppBar(navController: NavHostController) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val savedProfilePictureUri by preferencesManager.profilePictureUri.collectAsState(initial = "")

    var profileImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    // Load profile picture when URI changes
    LaunchedEffect(savedProfilePictureUri) {
        if (savedProfilePictureUri.isNotEmpty()) {
            try {
                val uri = Uri.parse(savedProfilePictureUri)
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    bitmap?.let {
                        profileImageBitmap = it.asImageBitmap()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                profileImageBitmap = null
            }
        } else {
            profileImageBitmap = null
        }
    }

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
                if (profileImageBitmap != null) {
                    Image(
                        bitmap = profileImageBitmap!!,
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile_pic),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GlobalTopBarColor)
    )
}
