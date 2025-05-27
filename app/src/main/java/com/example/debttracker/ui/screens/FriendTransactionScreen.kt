package com.example.debttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.ButtonVariant
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomEnumPickField
import com.example.debttracker.ui.components.CustomNumberField
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.theme.AppBackgroundColor
import com.example.debttracker.viewmodels.AddDebtViewModel
import com.example.debttracker.viewmodels.LoginViewModel
import com.example.debttracker.viewmodels.ViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun FriendTransactionScreen(
    navController: NavHostController,
    friendId: String,
    loginViewModel: LoginViewModel = viewModel(factory = ViewModelFactory(context = LocalContext.current)),
    addDebtViewModel: AddDebtViewModel = viewModel(factory = ViewModelFactory(loginViewModel, LocalContext.current))
) {
    val amount by addDebtViewModel.amount.observeAsState("")
    val currency by addDebtViewModel.currency.observeAsState("USD")
    val paysId by addDebtViewModel.pays.observeAsState("")
    val hasError by addDebtViewModel.hasError.observeAsState(false)
    val errorMessage by addDebtViewModel.errorMessage.observeAsState("")
    val successMessage by addDebtViewModel.successMessage.observeAsState("")
    val friendsWithEmails by addDebtViewModel.friendsWithEmails.observeAsState(emptyList())
    val conversionRate by addDebtViewModel.conversionRate.observeAsState(1.0)
    val availableCurrencies = addDebtViewModel.availableCurrencies
    val isLoadingFriends by addDebtViewModel.isLoadingFriends.observeAsState(false)
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }
    val isSubmitting = remember { mutableStateOf(false) }
    
    // Find friend's email for display
    val friendEmail = friendsWithEmails.find { it.first == friendId }?.second ?: "Loading..."
    val currentUserEmail = loginViewModel.currentUser.value?.email ?: "Me"
    
    // Effects
    LaunchedEffect(Unit) {
        println("DEBUG: LaunchedEffect in FriendTransactionScreen triggered")
        addDebtViewModel.resetState()
        
        // First ensure we have the latest user data with friends list
        loginViewModel.refreshUserData()
        
        // Give time for the data to be fetched and then load friends
        kotlinx.coroutines.delay(500)
        addDebtViewModel.loadFriendsWithEmails()
    }
    
    LaunchedEffect(currency) {
        if (currency != "PLN") {
            isLoading.value = true
            addDebtViewModel.fetchConversionRate()
            isLoading.value = false
        }
    }
    
    LaunchedEffect(hasError, errorMessage, successMessage) {
        if (hasError && errorMessage.isNotEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short
                )
                addDebtViewModel.errorMessage.value = ""
                addDebtViewModel.hasError.value = false
                isSubmitting.value = false
            }
        } else if (successMessage.isNotEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = successMessage,
                    duration = SnackbarDuration.Short
                )
                // Navigate back on success after a slight delay
                kotlinx.coroutines.delay(1000)
                isSubmitting.value = false
                navController.navigateUp()
            }
        }
    }
    
    Scaffold(
        modifier = Modifier.background(AppBackgroundColor),
        containerColor = AppBackgroundColor,
        topBar = { BackTopAppBar("Transaction with $friendEmail", navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoadingFriends) {
                CircularProgressIndicator()
                CustomText(
                    text = "Loading friend data...",
                    fontSize = 16.sp
                )
            } else {
                // Amount input with currency
                CustomText(
                    text = "Enter Transaction Amount",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                CustomNumberField(
                    value = amount,
                    onValueChange = { addDebtViewModel.amount.value = it },
                    label = "Amount",
                    placeholder = "Enter amount"
                )
                
                // Currency selection
                CustomEnumPickField(
                    label = "Currency",
                    options = availableCurrencies,
                    selectedOption = currency,
                    onOptionSelected = { addDebtViewModel.currency.value = it }
                )
                
                // Who paid selection (limited to current user and this friend)
                CustomText(
                    text = "Who paid?",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                val payerOptions = listOf(currentUserEmail, friendEmail)
                CustomEnumPickField(
                    label = "Paid by",
                    options = payerOptions,
                    selectedOption = if (paysId == friendId) friendEmail else currentUserEmail,
                    onOptionSelected = { 
                        // Set the actual ID, not the email
                        if (it == friendEmail) {
                            addDebtViewModel.pays.value = friendId
                        } else {
                            addDebtViewModel.pays.value = loginViewModel.currentUser.value?.uid ?: ""
                        }
                        // Also set the indebted to the opposite person
                        if (it == friendEmail) {
                            addDebtViewModel.indebted.value = loginViewModel.currentUser.value?.uid ?: ""
                        } else {
                            addDebtViewModel.indebted.value = friendId
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                CustomButton(
                    variant = ButtonVariant.LIME,
                    text = "Add Transaction",
                    enabled = !isLoading.value && amount.isNotEmpty() && paysId.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(0.7f),
                    onClick = {
                        isSubmitting.value = true
                        addDebtViewModel.addTransaction()
                        loginViewModel.refreshUserData()
                    }
                )
            }
        }
    }
}