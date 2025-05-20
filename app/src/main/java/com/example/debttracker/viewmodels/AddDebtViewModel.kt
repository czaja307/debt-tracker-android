package com.example.debttracker.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.filterNotNull

/**
 * ViewModel for adding transactions between users
 * Handles form validation, currency conversion, and transaction creation
 */
class AddDebtViewModel(private val loginViewModel: LoginViewModel) : ViewModel() {
    val pays = MutableLiveData<String>()
    val indebted = MutableLiveData<String>()
    val amount = MutableLiveData("")
    val currency = MutableLiveData("PLN")
    val hasError = MutableLiveData(false)
    val errorMessage = MutableLiveData("")
    val successMessage = MutableLiveData("")
    val conversionRate = MutableLiveData(1.0)
    val friendsWithEmails = MutableLiveData<List<Pair<String, String>>>(emptyList())
    val isLoadingFriends = MutableLiveData(false)
    
    val availableCurrencies = listOf("PLN", "USD", "EUR", "GBP", "CZK")
    private val apiKey: String = "fca_live_9r3DTzOKWo8YyvDndrpNu9Rl2rELohMD3VuxJBOj"
    
    init {
        println("DEBUG: AddDebtViewModel init called")
        viewModelScope.launch {
            // Wait a bit to make sure the LoginViewModel has loaded its data
            kotlinx.coroutines.delay(500)
            loadFriendsWithEmails()
        }
        // Reload friends list whenever storedUser is updated
        viewModelScope.launch {
            loginViewModel.storedUser.asFlow()
                .filterNotNull()
                .collect {
                    loadFriendsWithEmails()
                }
        }
    }
    
    /**
     * Loads the current user and their friends with email addresses
     * Used to populate dropdown menus for transaction participants
     */
    fun loadFriendsWithEmails() {
        viewModelScope.launch {
            isLoadingFriends.postValue(true)
            val friendList = mutableListOf<Pair<String, String>>()

            // Add current user as "Me"
            loginViewModel.currentUser.value?.let { user ->
                friendList.add(user.uid to "Me")
            }

            // Use friends list from storedUser to populate friends
            val friendUIDs = loginViewModel.storedUser.value?.friends.orEmpty()
            friendUIDs.forEach { friendUID ->
                val email = withContext(Dispatchers.IO) {
                    loginViewModel.fetchUserEmail(friendUID)
                }
                friendList.add(friendUID to email)
            }

            friendsWithEmails.postValue(friendList)
            isLoadingFriends.postValue(false)
        }
    }
    
    /**
     * Fetches currency conversion rate from the API
     * Converts selected currency to PLN which is the base currency for all transactions
     */
    fun fetchConversionRate() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val endpoint = "https://api.freecurrencyapi.com/v1/latest"
                val urlString = "$endpoint?apikey=$apiKey&base_currency=${currency.value}&currencies=PLN"
                
                val url = URL(urlString)
                val connection = url.openConnection() as HttpsURLConnection
                connection.requestMethod = "GET"
                
                if (connection.responseCode == HttpsURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(response)
                    val dataObject = jsonObject.getJSONObject("data")
                    val rate = dataObject.getDouble("PLN")
                    
                    withContext(Dispatchers.Main) {
                        conversionRate.value = rate
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        hasError.value = true
                        errorMessage.value = "API Error: ${connection.responseCode}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    hasError.value = true
                    errorMessage.value = "Network Error: ${e.localizedMessage}"
                }
            }
        }
    }
    
    /**
     * Validates and creates a new transaction between users
     * Performs amount validation, user selection validation, and converts currency if needed
     */
    fun addTransaction() {
        val amountValue = amount.value?.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            hasError.postValue(true)
            errorMessage.postValue("Please enter a valid amount greater than zero")
            return
        }
        
        val paysValue = pays.value
        val indebtedValue = indebted.value
        if (paysValue.isNullOrEmpty() || indebtedValue.isNullOrEmpty()) {
            hasError.postValue(true)
            errorMessage.postValue("Please select a person")
            return
        }
        
        val isPayerCurrentUser = paysValue == loginViewModel.currentUser.value?.uid
        val isIndebtedCurrentUser = indebtedValue == loginViewModel.currentUser.value?.uid
        
        if (isPayerCurrentUser == isIndebtedCurrentUser) {
            hasError.postValue(true)
            errorMessage.postValue("Invalid people assigned")
            return
        }
        
        val amountInPLN = amountValue * (conversionRate.value ?: 1.0)
        val transactionUID = if (isPayerCurrentUser) indebtedValue else paysValue
        
        viewModelScope.launch {
            try {
                loginViewModel.addTransaction(transactionUID, amountInPLN, paysValue)
                successMessage.postValue("Transaction added successfully!")
                
                // Reset form fields for next use
                amount.postValue("")
                currency.postValue("PLN")
                conversionRate.postValue(1.0)
            } catch (e: Exception) {
                hasError.postValue(true)
                errorMessage.postValue("Failed to add transaction: ${e.localizedMessage}")
            }
        }
    }
    
    /**
     * Resets all state variables to their initial values
     * Should be called when initializing screens to prevent data leakage between sessions
     */
    fun resetState() {
        amount.value = ""
        currency.value = "PLN"
        pays.value = ""
        indebted.value = ""
        hasError.value = false
        errorMessage.value = ""
        successMessage.value = ""
        conversionRate.value = 1.0
    }
}
