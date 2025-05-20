package com.example.debttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
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
    loginViewModel: LoginViewModel = viewModel(),
    addDebtViewModel: AddDebtViewModel = viewModel(factory = ViewModelFactory(loginViewModel))
) {
    val amount by addDebtViewModel.amount.observeAsState("")
    val currency by addDebtViewModel.currency.observeAsState("PLN")
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
    
    // Find friend email
    val friendEmail = remember(friendsWithEmails) {
        friendsWithEmails.find { it.first == friendId }?.second ?: ""
    }
    
    // Find current user
    val currentUserInfo = remember(friendsWithEmails) {
        friendsWithEmails.find { it.second == "Me" }
    }
    
    // Payment options specific to this friend transaction
    val paymentOptions = listOf("I paid for $friendEmail", "$friendEmail paid for me")
    var selectedOption by remember { mutableStateOf(paymentOptions[0]) }
    
    // Effects
    LaunchedEffect(Unit) {
        println("DEBUG: LaunchedEffect in FriendTransactionScreen triggered for friendId: $friendId")
        addDebtViewModel.resetState()
        
        // First ensure we have the latest user data with friends list
        loginViewModel.refreshUserData()
        
        // Give time for the data to be fetched and then load friends
        kotlinx.coroutines.delay(500)
        addDebtViewModel.loadFriendsWithEmails()
        
        // Set friend ID immediately without waiting for user selection
        loginViewModel.currentUser.value?.let { user ->
            // Default to "I paid for friend" scenario
            println("DEBUG: Setting default transaction participants - currentUser: ${user.uid}, friend: $friendId")
            addDebtViewModel.pays.value = user.uid
            addDebtViewModel.indebted.value = friendId
        }
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
                // Navigate back on success
                isSubmitting.value = false
                navController.navigateUp()
            }
        }
    }

    Scaffold(
        modifier = Modifier.background(AppBackgroundColor),
        topBar = { BackTopAppBar("Transaction with $friendEmail", navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomNumberField(
                    value = amount,
                    onValueChange = { addDebtViewModel.amount.value = it },
                    label = "Enter an amount",
                    placeholder = "$0.00",
                    modifier = Modifier.fillMaxWidth()
                )

                CustomEnumPickField(
                    label = "Choose Currency",
                    options = availableCurrencies,
                    selectedOption = currency,
                    onOptionSelected = { addDebtViewModel.currency.value = it },
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (isLoading.value) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomText(text = "Fetching conversion rate...")
                } else if (currency != "PLN") {
                    CustomText(text = "Conversion rate to PLN: $conversionRate")
                }

                if (isLoadingFriends) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomText(text = "Loading friends...")
                    }
                } else {
                    CustomEnumPickField(
                        label = "Payment Direction",
                        options = paymentOptions,
                        selectedOption = selectedOption,
                        onOptionSelected = { selected ->
                            selectedOption = selected
                            // Set the payer and indebted based on selection
                            currentUserInfo?.let { (currentUserId, _) ->
                                if (selected == paymentOptions[0]) {
                                    // I paid for friend
                                    addDebtViewModel.pays.value = currentUserId
                                    addDebtViewModel.indebted.value = friendId
                                } else {
                                    // Friend paid for me
                                    addDebtViewModel.pays.value = friendId
                                    addDebtViewModel.indebted.value = currentUserId
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    CustomButton(
                        variant = ButtonVariant.LIME,
                        icon = Icons.Default.Add,
                        text = if (isSubmitting.value) "Adding..." else "Add Transaction",
                        onClick = {
                            isSubmitting.value = true
                            addDebtViewModel.addTransaction()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSubmitting.value
                    )
                }
            }
        }
    )
}
