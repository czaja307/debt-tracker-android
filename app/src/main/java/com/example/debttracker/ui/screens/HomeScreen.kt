package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.debttracker.data.PreferencesManager
import com.example.debttracker.ui.components.BalanceField
import com.example.debttracker.ui.components.CustomBottomSheetScaffold
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.DebtOverTimeGraph
import com.example.debttracker.ui.components.DebtPieChart
import com.example.debttracker.ui.components.GlobalTopAppBar
import com.example.debttracker.ui.components.getCurrencySymbol
import com.example.debttracker.viewmodels.LoginViewModel
import com.example.debttracker.viewmodels.ViewModelFactory
import com.github.tehras.charts.piechart.PieChartData

@Composable
fun HomeScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(factory = ViewModelFactory(context = LocalContext.current))
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val userCurrency by preferencesManager.userCurrency.collectAsState(initial = "USD")
    val currencySymbol = getCurrencySymbol(userCurrency)
    val totalBalance by loginViewModel.totalBalance.observeAsState(0.0)
    
    // Fetch latest data when the screen is shown
    LaunchedEffect(Unit) {
        loginViewModel.refreshUserData()
    }

    CustomBottomSheetScaffold(
        topBar = { GlobalTopAppBar(navController) },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                CustomText(
                    text = if (totalBalance >= 0) "People owe you" else "You owe",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomText(
                    text = "$currencySymbol${"%.2f".format(kotlin.math.abs(totalBalance))}",
                    fontSize = 64.sp
                )
            }
        },
        sheetContent = {
            // Get pie chart data based on debts
            val positiveBalance = if (totalBalance > 0) totalBalance.toFloat() else 0f
            val negativeBalance = if (totalBalance < 0) kotlin.math.abs(totalBalance.toFloat()) else 0f
            
            val dataPieChart = mapOf(
                "To me" to PieChartData.Slice(
                    value = positiveBalance,
                    color = Color(0xFF3B4C00)  // Darker green
                ),
                "I owe" to PieChartData.Slice(
                    value = negativeBalance,
                    color = Color(0xFFB4DD1E)  // Lighter green
                ),
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BalanceField(
                    label = "Total debt to me", 
                    balance = if (totalBalance > 0) totalBalance.toFloat() else 0f,
                    currencySymbol = currencySymbol
                )
                // Create a simple sample data map for the debt over time graph
                // This should ideally come from the ViewModel with real debt history data
                val timeData = mapOf(
                    "Jan" to 20f,
                    "Feb" to 35f,
                    "Mar" to 25f,
                    "Apr" to kotlin.math.abs(totalBalance.toFloat())  // Current balance
                )
                DebtOverTimeGraph(data = timeData)
                DebtPieChart(dataPieChart, "Current debt", currencySymbol)
                BalanceField(
                    label = "My total debt", 
                    balance = if (totalBalance < 0) kotlin.math.abs(totalBalance.toFloat()) else 0f,
                    currencySymbol = currencySymbol
                )
                BalanceField(
                    label = "People owe you", 
                    balance = if (totalBalance > 0) totalBalance.toFloat() else 0f,
                    currencySymbol = currencySymbol
                )
                //TransactionField(date = "2025-04-08", amount = "$50.00")
                //CustomButton(icon = Icons.Filled.Info, text = "Test Button")
                //CustomTextField(label = "Description", text = textValue, onTextChange = { textValue = it })
            }
        },
    )
}



