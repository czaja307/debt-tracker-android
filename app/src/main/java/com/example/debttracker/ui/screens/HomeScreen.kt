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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.BalanceField
import com.example.debttracker.ui.components.CustomBottomSheetScaffold
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.DebtOverTimeGraph
import com.example.debttracker.ui.components.DebtPieChart
import com.example.debttracker.ui.components.GlobalTopAppBar
import com.github.tehras.charts.piechart.PieChartData
import com.example.debttracker.viewmodels.LoginViewModel
import kotlin.math.absoluteValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    // Observe stored user and compute real balances
    val storedUser by loginViewModel.storedUser.observeAsState()
    val balances = storedUser?.transactions?.mapValues { (friendId, _) ->
        loginViewModel.calculateBalance(friendId).toFloat()
    }.orEmpty()
    val totalOwedToMe = balances.values.filter { it > 0f }
        .sumOf { it.toDouble() }.toFloat()
    val totalYouOwe = balances.values.filter { it < 0f }
        .sumOf { it.toDouble() }.absoluteValue.toFloat()
        
    // Refresh data when screen becomes active
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
                    text = "You owe",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomText(
                    text = if (totalYouOwe > 0f) "-$${"%.2f".format(totalYouOwe)}" else "$0.00",
                    fontSize = 64.sp
                )
            }
        },
        sheetContent = {
            // Prepare line chart data: aggregate debt over time
            val historyData = remember(storedUser) {
                val result = storedUser?.transactions?.values
                    ?.flatten()
                    ?.sortedBy { it.date.time } // Sort by timestamp to avoid operator ambiguity
                    ?.groupBy { txn -> 
                        try {
                            SimpleDateFormat("dd MMM", Locale.getDefault()).format(txn.date)
                        } catch (e: Exception) {
                            "Unknown" // Fallback if date formatting fails
                        }
                    }
                    ?.mapValues { entry ->
                        entry.value.sumOf { txn ->
                            val uid = loginViewModel.currentUser.value?.uid ?: ""
                            val amt = if (txn.paidBy == uid) txn.amount else -txn.amount
                            amt
                        }.toFloat()
                    } ?: emptyMap()
                
                // Ensure we have some data for the chart
                if (result.isEmpty()) {
                    mapOf("No Data" to 0f)
                } else {
                    result
                }
            }
            // Pie chart slices from real balances
            val dataPieChart = if (totalOwedToMe > 0f || totalYouOwe > 0f) {
                mapOf(
                    "To me" to PieChartData.Slice(
                        value = totalOwedToMe.coerceAtLeast(0.01f), // Ensure at least minimal value for rendering
                        color = Color(0xFF3B4C00)
                    ),
                    "My" to PieChartData.Slice(
                        value = totalYouOwe.coerceAtLeast(0.01f), // Ensure at least minimal value for rendering
                        color = Color(0xFFB4DD1E)
                    )
                )
            } else {
                // Provide dummy data if no real balances
                mapOf("No Data" to PieChartData.Slice(value = 1f, color = Color.Gray))
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BalanceField("People owe you", balance = totalOwedToMe)
                DebtOverTimeGraph(data = historyData)
                DebtPieChart(dataPieChart, "Current debt")
                BalanceField("My total debt", balance = totalYouOwe)
                BalanceField("Total debt to me", balance = totalOwedToMe)
            }
        },
    )
}



