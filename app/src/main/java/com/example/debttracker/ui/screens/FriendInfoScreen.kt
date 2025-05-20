package com.example.debttracker.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BackTopAppBar
import com.example.debttracker.ui.components.ButtonVariant
import com.example.debttracker.ui.components.ColorBalanceText
import com.example.debttracker.ui.components.CustomBottomSheetScaffold
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.CustomUserAvatar
import com.example.debttracker.ui.components.TransactionField
import com.example.debttracker.ui.theme.AppBackgroundColor
import com.example.debttracker.viewmodels.LoginViewModel
import com.example.debttracker.models.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FriendInfoScreen(
    navController: NavHostController,
    friendId: String,
    loginViewModel: LoginViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val storedUser by loginViewModel.storedUser.observeAsState()
    val transactions = storedUser?.transactions?.get(friendId).orEmpty()
    val currentUser by loginViewModel.currentUser.observeAsState()
    val friendEmail by produceState(initialValue = "", friendId) {
        value = loginViewModel.fetchUserEmail(friendId)
    }
    val friendBalance = loginViewModel.calculateBalance(friendId).toFloat()
    val title = "${friendEmail}'s info"
    
    // Refresh data when screen becomes active
    LaunchedEffect(friendId) {
        // Ensure we have the latest transaction data
        loginViewModel.refreshUserData()
    }

    CustomBottomSheetScaffold(
        topBar = { BackTopAppBar(title, navController) },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .background(AppBackgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                CustomUserAvatar(
                    imageRes = null,
                    editable = false,
                    onEditClick = { },
                    modifier = Modifier.size(172.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                ColorBalanceText(balance = friendBalance, fontSize = 56.sp)
                Spacer(modifier = Modifier.height(16.dp))
                CustomButton(
                    variant = ButtonVariant.LIME,
                    icon = Icons.Default.Add,
                    text = "New",
                    onClick = { 
                        navController.navigate("friend_transaction/$friendId") 
                    },
                    fontSize = 24.sp,
                    aspectRatio = 3f,
                    buttonWidth = 200f
                )
            }
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomText(
                        text = "History",
                        fontSize = 28.sp,
                        color = androidx.compose.ui.graphics.Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Display actual transactions
                transactions.forEach { txn ->
                    // Format transaction date and amount using current user state
                    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(txn.date)
                    val amt = if (txn.paidBy == currentUser?.uid)
                        "+$${txn.amount}"
                    else
                        "-$${txn.amount}"
                    TransactionField(date = date, amount = amt)
                }
            }
        }
    )
}
