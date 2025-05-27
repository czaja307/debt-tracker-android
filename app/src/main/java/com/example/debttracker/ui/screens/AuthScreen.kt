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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.ButtonVariant
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomEnumPickField
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.CustomTextField
import com.example.debttracker.ui.components.CustomUserAvatar
import com.example.debttracker.ui.theme.AppBackgroundColor
import com.example.debttracker.viewmodels.LoginViewModel
import com.example.debttracker.viewmodels.ViewModelFactory
import java.io.InputStream

/**
 * Root authentication host – decides which screen to show based on LoginViewModel state.
 */
@Composable
fun AuthHost(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(factory = ViewModelFactory(context = LocalContext.current))
) {
    val isLoggedIn by loginViewModel.isLoggedIn.observeAsState(false)
    val showSignup by loginViewModel.showSignupView.observeAsState(false)

    if (isLoggedIn) {
        ProfileContent(navController)
    } else {
        if (showSignup) {
            SignInScreen(navController, loginViewModel)
        } else {
            LoginScreen(navController, loginViewModel)
        }
    }
}