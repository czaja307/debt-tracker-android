package com.example.debttracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.debttracker.ui.components.GlobalTopAppBar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.debttracker.ui.components.BalanceField
import com.example.debttracker.ui.components.CustomBottomSheetScaffold
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.DebtOverTimeGraph
import com.example.debttracker.ui.components.DebtWheelGraph
import com.example.debttracker.ui.theme.LimeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = { GlobalTopAppBar(navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomBottomSheetScaffold(
                    content = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomText(
                                text = "You owe",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            CustomText(
                                text = "$12.44",
                                fontSize = 64.sp
                            )
                        }
                    },
                    sheetContent = {
                        var textValue by remember { mutableStateOf("") }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            BalanceField("People owe you", balance = 123.45f)
                            DebtOverTimeGraph()
                            DebtWheelGraph()
                            BalanceField("My total debt", balance = 123.45f)
                            BalanceField("Total debt to me", balance = 123.45f)
                            //TransactionField(date = "2025-04-08", amount = "$50.00")
                            //CustomButton(icon = Icons.Filled.Info, text = "Test Button")
                            //CustomTextField(label = "Description", text = textValue, onTextChange = { textValue = it })
                        }
                    },
                )
            }
        }
    )
}


