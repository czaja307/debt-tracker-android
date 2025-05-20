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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun NewTransactionScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(),
    addDebtViewModel: AddDebtViewModel = viewModel(factory = ViewModelFactory(loginViewModel))
) {
    val amount by addDebtViewModel.amount.observeAsState("")
    val currency by addDebtViewModel.currency.observeAsState("PLN")
    val paysId by addDebtViewModel.pays.observeAsState("")
    val indebtedId by addDebtViewModel.indebted.observeAsState("")
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
    
    // Format friends list for the dropdown menu
    val friendOptions = friendsWithEmails.map { (_, email) -> email }
    
    // Get indices for selected people
    val paysIndex = friendsWithEmails.indexOfFirst { it.first == paysId }
    val indebtedIndex = friendsWithEmails.indexOfFirst { it.first == indebtedId }
    
    // Selected options for the UI
    val selectedPayer = if (paysIndex >= 0) friendOptions[paysIndex] else ""
    val selectedIndebted = if (indebtedIndex >= 0) friendOptions[indebtedIndex] else ""
    
    // Effects
    LaunchedEffect(Unit) {
        println("DEBUG: LaunchedEffect in NewTransactionScreen triggered")
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
                // Navigate back on success
                isSubmitting.value = false
                navController.navigateUp()
            }
        }
    }

    Scaffold(
        modifier = Modifier.background(AppBackgroundColor),
        topBar = { BackTopAppBar("Add a new transaction", navController) },
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
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomText(text = "Loading friends...")
                    }
                } else if (friendOptions.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CustomText(
                            text = "You don't have any friends yet",
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomButton(
                            variant = ButtonVariant.GREY,
                            text = "Add Friends",
                            onClick = { navController.navigate("add_friend") },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomButton(
                            variant = ButtonVariant.GREY,
                            text = "Refresh Friends List",
                            onClick = { 
                                scope.launch {
                                    loginViewModel.refreshUserData()
                                    kotlinx.coroutines.delay(500)
                                    addDebtViewModel.loadFriendsWithEmails()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                    }
                } else {
                    CustomEnumPickField(
                        label = "Who was paying?",
                        options = friendOptions,
                        selectedOption = selectedPayer,
                        onOptionSelected = { selected ->
                            val index = friendOptions.indexOf(selected)
                            if (index >= 0) {
                                addDebtViewModel.pays.value = friendsWithEmails[index].first
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    CustomEnumPickField(
                        label = "Who was in debt?",
                        options = friendOptions,
                        selectedOption = selectedIndebted,
                        onOptionSelected = { selected ->
                            val index = friendOptions.indexOf(selected)
                            if (index >= 0) {
                                addDebtViewModel.indebted.value = friendsWithEmails[index].first
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



