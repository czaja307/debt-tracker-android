package com.example.debttracker.ui.screens

//import com.example.debttracker.models.User
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.debttracker.R
import com.example.debttracker.ui.components.CustomButton
import com.example.debttracker.ui.components.CustomText
import com.example.debttracker.ui.components.FriendField
import com.example.debttracker.ui.components.getCurrencySymbol
import com.example.debttracker.ui.components.GlobalTopAppBar
import com.example.debttracker.ui.theme.AppBackgroundColor
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.debttracker.models.User
import com.example.debttracker.viewmodels.LoginViewModel
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import com.example.debttracker.data.PreferencesManager

// Local model for displaying friends
data class FriendDisplay(
    val id: String,
    val name: String,
    val balance: Float,
    val imageRes: Int? = null
)

@Composable
fun FriendsScreen(navController: NavHostController, loginViewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val userCurrency by preferencesManager.userCurrency.collectAsState(initial = "USD")
    val currencySymbol = getCurrencySymbol(userCurrency)
    
    Scaffold(
        modifier = Modifier.background(AppBackgroundColor),
        topBar = { GlobalTopAppBar(navController) },
        content = { innerPadding ->
            // observe stored FirestoreUser
            val storedUser by loginViewModel.storedUser.observeAsState()
            val friendIds = storedUser?.friends.orEmpty()
            // produce list of FriendDisplay by fetching emails and balances
            val friendDisplays by produceState(initialValue = emptyList<FriendDisplay>(), friendIds) {
                val list = friendIds.map { id ->
                    val email = loginViewModel.fetchUserEmail(id)
                    val bal = loginViewModel.calculateBalance(id).toFloat()
                    FriendDisplay(id = id, name = email, balance = bal)
                }
                value = list
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomButton(
                        text = "Add Friend",
                        onClick = { navController.navigate("add_friend") },
                        aspectRatio = 4f,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    CustomButton(
                        text = "Invitations",
                        onClick = { navController.navigate("invitations") },
                        aspectRatio = 4f,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (friendDisplays.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CustomText(
                            text = "You have no friends added",
                            fontSize = 20.sp,
                        )
                    }
                } else {
                    friendDisplays.forEach { friend ->
                        FriendField(friend = friend, navController = navController, currencySymbol = currencySymbol)
                    }
                }
            }
        }
    )
}
