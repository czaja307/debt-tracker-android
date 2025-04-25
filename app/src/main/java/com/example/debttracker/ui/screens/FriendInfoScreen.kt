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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun FriendInfoScreen(navController: NavHostController, friendId: String) {
    val scrollState = rememberScrollState()
    val friendBalance = 15.50f
    val friend = exampleUserList[friendId.toInt() - 1]

    val title = "${friend.name}'s info"

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
                    imageRes = friend.imageRes,
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
                    onClick = { },
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
                TransactionField(date = "14 Apr 2025", amount = "$10.00")
                TransactionField(date = "13 Apr 2025", amount = "$-5.00")
                TransactionField(date = "12 Apr 2025", amount = "$20.00")
                TransactionField(date = "14 Apr 2025", amount = "$10.00")
                TransactionField(date = "13 Apr 2025", amount = "$-5.00")
                TransactionField(date = "12 Apr 2025", amount = "$20.00")
                TransactionField(date = "14 Apr 2025", amount = "$10.00")
                TransactionField(date = "13 Apr 2025", amount = "$-5.00")
                TransactionField(date = "12 Apr 2025", amount = "$20.00")
                TransactionField(date = "14 Apr 2025", amount = "$10.00")
                TransactionField(date = "13 Apr 2025", amount = "$-5.00")
                TransactionField(date = "12 Apr 2025", amount = "$20.00")
                TransactionField(date = "14 Apr 2025", amount = "$10.00")
                TransactionField(date = "13 Apr 2025", amount = "$-5.00")
                TransactionField(date = "12 Apr 2025", amount = "$20.00")
                TransactionField(date = "14 Apr 2025", amount = "$10.00")
                TransactionField(date = "13 Apr 2025", amount = "$-5.00")
                TransactionField(date = "12 Apr 2025", amount = "$20.00")
            }
        }
    )
}
