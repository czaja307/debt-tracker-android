package com.example.debttracker.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.ButtonVariant
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomEnumPickField
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.CustomTextField
import com.example.debttracker.ui.components.CustomUserAvatar
import java.io.InputStream

@Composable
fun ProfileSettingsScreen(navController: NavHostController) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(true) }

    if (isLoggedIn) {
        ProfileContent(navController)
    } else {
        if (isLoginMode) {
            LoginScreen(
                onSwitchToSignIn = { isLoginMode = false },
                onLoginSuccess = { isLoggedIn = true },
                navController = navController
            )
        } else {
            SignInScreen(
                onSwitchToLogin = { isLoginMode = true },
                onSignInSuccess = { isLoggedIn = true },
                navController = navController
            )
        }
    }
}

@Composable
fun ProfileContent(navController: NavHostController) {

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var defaultCurrency by remember { mutableStateOf("USD") }
    val currencyOptions = listOf("USD", "EUR", "GBP")
    var avatarImage by remember { mutableStateOf<ImageBitmap?>(null) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult (
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = inputStream?.let { BitmapFactory.decodeStream(it) }
            avatarImage = bitmap?.asImageBitmap()
            inputStream?.close()
        }
    }

    Scaffold(
        topBar = {
            BackTopAppBar(title = "Profile Settings", navController = navController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomUserAvatar(
                    image = avatarImage,
                    editable = true,
                    onEditClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name",
                    placeholder = "Enter your name"
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    placeholder = "Enter your username"
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomEnumPickField(
                    label = "Default Currency",
                    options = currencyOptions,
                    selectedOption = defaultCurrency,
                    onOptionSelected = { defaultCurrency = it }
                )
            }
        }
    )
}

@Composable
fun LoginScreen(
    onSwitchToSignIn: () -> Unit,
    onLoginSuccess: () -> Unit,
    navController: NavHostController
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            BackTopAppBar(title = "Login", navController = navController)
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomText(
                            text = "Login",
                            fontSize = 32.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        CustomTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Username",
                            placeholder = "Enter username"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            placeholder = "Enter password"
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomButton(
                            variant = ButtonVariant.GREY,
                            text = "Sign In",
                            onClick = onSwitchToSignIn
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomButton(
                            variant = ButtonVariant.LIME,
                            text = "Log In",
                            onClick = onLoginSuccess
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun SignInScreen(
    onSwitchToLogin: () -> Unit,
    onSignInSuccess: () -> Unit,
    navController: NavHostController
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            BackTopAppBar(title = "Sign In", navController = navController)
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomText(
                            text = "Sign In",
                            fontSize = 32.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        CustomTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Name",
                            placeholder = "Enter name"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Username",
                            placeholder = "Enter username"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            placeholder = "Enter password"
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomButton(
                            variant = ButtonVariant.GREY,
                            text = "Log In",
                            onClick = onSwitchToLogin
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomButton(
                            variant = ButtonVariant.LIME,
                            text = "Sign In",
                            onClick = onSignInSuccess
                        )
                    }
                }
            }
        }
    )
}

