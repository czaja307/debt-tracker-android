package com.example.debttracker.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.debttracker.data.PreferencesManager
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.ButtonVariant
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomEnumPickField
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.CustomTextField
import com.example.debttracker.ui.components.CustomUserAvatar
import com.example.debttracker.ui.theme.AppBackgroundColor
import com.example.debttracker.viewmodels.LoginViewModel
import kotlinx.coroutines.launch
import java.io.InputStream
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomEnumPickField
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.CustomTextField
import com.example.debttracker.ui.components.CustomUserAvatar
import com.example.debttracker.ui.theme.AppBackgroundColor


@Composable
fun ProfileContent(navController: NavHostController, loginViewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Get saved preferences
    val savedName by preferencesManager.userName.collectAsState(initial = "")
    val savedCurrency by preferencesManager.userCurrency.collectAsState(initial = "USD")
    
    var name by remember { mutableStateOf("") }
    var defaultCurrency by remember { mutableStateOf("USD") }
    val currencyOptions = listOf("USD", "EUR", "GBP", "PLN")
    var avatarImage by remember { mutableStateOf<ImageBitmap?>(null) }

    // Load saved preferences
    LaunchedEffect(savedName, savedCurrency) {
        name = savedName
        defaultCurrency = savedCurrency
    }

    val launcher = rememberLauncherForActivityResult(
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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

                CustomEnumPickField(
                    label = "Default Currency",
                    options = currencyOptions,
                    selectedOption = defaultCurrency,
                    onOptionSelected = { defaultCurrency = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                CustomButton(
                    variant = ButtonVariant.LIME,
                    text = "Save",
                    onClick = {
                        coroutineScope.launch {
                            preferencesManager.saveUserName(name)
                            preferencesManager.saveUserCurrency(defaultCurrency)
                            snackbarHostState.showSnackbar("Settings saved successfully")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                CustomButton(
                    variant = ButtonVariant.GREY,
                    text = "Logout",
                    onClick = {
                        loginViewModel.signOut()
                        navController.navigate("auth") {
                            popUpTo(navController.graph.startDestinationRoute ?: "home") { inclusive = true }
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun LoginScreen(navController: NavHostController, loginViewModel: LoginViewModel) {
    val email by loginViewModel.email.observeAsState("")
    val password by loginViewModel.password.observeAsState("")
    val hasError by loginViewModel.hasError.observeAsState(false)
    val errorMessage by loginViewModel.errorMessage.observeAsState("")

    Scaffold(
        modifier = Modifier.background(AppBackgroundColor),
        topBar = { BackTopAppBar(title = "Login", navController = navController) }
    ) { innerPadding ->
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
                        value = email,
                        onValueChange = { loginViewModel.email.value = it },
                        label = "Email",
                        placeholder = "Enter email"
                    )
                    Spacer(Modifier.height(8.dp))
                    CustomTextField(
                        value = password,
                        onValueChange = { loginViewModel.password.value = it },
                        label = "Password",
                        placeholder = "Enter password"
                    )
                    if (hasError) {
                        Spacer(Modifier.height(8.dp))
                        CustomText(text = errorMessage, color = Color.Red, fontSize = 14.sp)
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomButton(
                        variant = ButtonVariant.GREY,
                        text = "Sign In",
                        onClick = { loginViewModel.showSignupView.value = true }
                    )
                    Spacer(Modifier.height(8.dp))
                    CustomButton(
                        variant = ButtonVariant.LIME,
                        text = "Log In",
                        onClick = { loginViewModel.signIn() }
                    )
                }
            }
        }
    }
}
@Composable
fun SignInScreen(navController: NavHostController, loginViewModel: LoginViewModel) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf("") }
    val email by loginViewModel.email.observeAsState("")
    val password by loginViewModel.password.observeAsState("")
    var defaultCurrency by remember { mutableStateOf("USD") }
    val currencyOptions = listOf("USD", "EUR", "GBP", "PLN")

    val hasError by loginViewModel.hasError.observeAsState(false)
    val errorMessage by loginViewModel.errorMessage.observeAsState("")

    Scaffold(
        modifier = Modifier.background(AppBackgroundColor),
        topBar = { BackTopAppBar(title = "Sign In", navController = navController) }
    ) { innerPadding ->
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
                    Spacer(Modifier.height(8.dp))
                    CustomTextField(
                        value = email,
                        onValueChange = { loginViewModel.email.value = it },
                        label = "Email",
                        placeholder = "Enter email"
                    )
                    Spacer(Modifier.height(8.dp))
                    CustomTextField(
                        value = password,
                        onValueChange = { loginViewModel.password.value = it },
                        label = "Password",
                        placeholder = "Enter password"
                    )
                    Spacer(Modifier.height(8.dp))
                    CustomEnumPickField(
                        label = "Default Currency",
                        options = currencyOptions,
                        selectedOption = defaultCurrency,
                        onOptionSelected = { defaultCurrency = it }
                    )
                    if (hasError) {
                        Spacer(Modifier.height(8.dp))
                        CustomText(text = errorMessage, color = Color.Red, fontSize = 14.sp)
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomButton(
                        variant = ButtonVariant.GREY,
                        text = "Log In",
                        onClick = { loginViewModel.showSignupView.value = false }
                    )
                    Spacer(Modifier.height(8.dp))
                    CustomButton(
                        variant = ButtonVariant.LIME,
                        text = "Sign In",
                        onClick = {
                            coroutineScope.launch {
                                // Save user preferences locally
                                preferencesManager.saveUserName(name)
                                preferencesManager.saveUserCurrency(defaultCurrency)
                                loginViewModel.signUp()
                            }
                        }
                    )
                }
            }
        }
    }
}