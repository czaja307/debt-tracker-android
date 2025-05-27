package com.example.debttracker.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.debttracker.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * ViewModel for handling debt transactions with currency conversion
 */
class AddDebtViewModel(
    private val loginViewModel: LoginViewModel,
    private val context: Context? = null
) : ViewModel() {

    private val preferencesManager by lazy { context?.let { PreferencesManager(it) } }
    private val apiKey: String = "fca_live_9r3DTzOKWo8YyvDndrpNu9Rl2rELohMD3VuxJBOj"
    
    val amount = MutableLiveData("")
    val currency = MutableLiveData("PLN")
    val pays = MutableLiveData("")
    val indebted = MutableLiveData("")
    val conversionRate = MutableLiveData(1.0)
    val hasError = MutableLiveData(false)
    val errorMessage = MutableLiveData("")
    val successMessage = MutableLiveData("")
    val friendsWithEmails = MutableLiveData<List<Pair<String, String>>>(emptyList())
    val availableCurrencies = listOf("PLN", "USD", "EUR", "GBP", "CZK")
    val isLoadingFriends = MutableLiveData(false)

    init {
        viewModelScope.launch {
            loadCurrency()
            loadFriends()
        }
    }

    /**
     * Load user's preferred currency from preferences
     */
    private suspend fun loadCurrency() {
        context?.let { ctx ->
            preferencesManager?.let { preferences ->
                val savedCurrency = preferences.userCurrency.first()
                currency.postValue(savedCurrency)
            }
        }
    }
    
    /**
     * Load friends list with their emails
     */
    private suspend fun loadFriends() {
        try {
            val friendIds = loginViewModel.getFriendsList()
            val friendsWithEmails = friendIds.map { id ->
                val email = loginViewModel.fetchUserEmail(id)
                id to email
            }
            this.friendsWithEmails.postValue(friendsWithEmails)
        } catch (e: Exception) {
            hasError.postValue(true)
            errorMessage.postValue("Error loading friends: ${e.localizedMessage}")
        }
    }
    
    /**
     * Add a new transaction with proper currency conversion
     */
    fun addTransaction() {
        viewModelScope.launch {
            try {
                hasError.postValue(false)
                
                // Validate input
                val amountValue = amount.value?.toDoubleOrNull()
                if (amountValue == null || amountValue <= 0) {
                    hasError.postValue(true)
                    errorMessage.postValue("Please enter a valid amount")
                    return@launch
                }
                
                if (pays.value.isNullOrEmpty()) {
                    hasError.postValue(true)
                    errorMessage.postValue("Please select who paid")
                    return@launch
                }
                
                if (indebted.value.isNullOrEmpty()) {
                    hasError.postValue(true)
                    errorMessage.postValue("Please select who is indebted")
                    return@launch
                }
                
                // Convert to PLN for storage (app standardizes on PLN internally)
                val amountInPLN = if (currency.value == "PLN") {
                    amountValue
                } else {
                    convertToPLN(amountValue, currency.value ?: "PLN")
                }
                
                // Add the transaction through LoginViewModel
                loginViewModel.addTransaction(
                    indebted.value ?: "",
                    amountInPLN,
                    pays.value ?: ""
                )

                // Check if loginViewModel encountered an error
                if (loginViewModel.hasError.value == true) {
                    hasError.postValue(true)
                    errorMessage.postValue(loginViewModel.errorMessage.value ?: "Transaction failed.")
                } else {
                    // Clear fields and show success message
                    amount.postValue("")
                    // Consider clearing indebted and pays fields if that's the desired UX, e.g.:
                    // indebted.postValue("")
                    // pays.postValue("")
                    successMessage.postValue("Transaction added successfully")
                }
            } catch (e: Exception) {
                hasError.postValue(true)
                errorMessage.postValue(e.localizedMessage ?: "Error adding transaction")
            }
        }
    }
    
    /**
     * Convert amount from selected currency to PLN
     */
    private suspend fun convertToPLN(amount: Double, fromCurrency: String): Double {
        return try {
            val rates = withContext(Dispatchers.IO) {
                fetchConversionRates("PLN")
            }
            
            val rate = rates[fromCurrency]
            if (rate != null) {
                amount * rate
            } else {
                // Fallback conversion if API fails
                val fallbackRate = when (fromCurrency) {
                    "USD" -> 4.0
                    "EUR" -> 4.35
                    "GBP" -> 5.0
                    "CZK" -> 0.175
                    else -> 1.0 // Default to no conversion
                }
                amount * fallbackRate
            }
        } catch (e: Exception) {
            // Fallback conversion if API fails
            val fallbackRate = when (fromCurrency) {
                "USD" -> 4.0
                "EUR" -> 4.35
                "GBP" -> 5.0
                "CZK" -> 0.175
                else -> 1.0 // Default to no conversion
            }
            amount * fallbackRate
        }
    }
    
    /**
     * Fetch conversion rates from currency API
     */
    private suspend fun fetchConversionRates(baseCurrency: String): Map<String, Double> {
        return try {
            val endpoint = "https://api.freecurrencyapi.com/v1/latest"
            val urlString = "$endpoint?apikey=$apiKey&base_currency=$baseCurrency&currencies=PLN,USD,EUR,GBP,CZK"
            
            val url = URL(urlString)
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            
            if (connection.responseCode == HttpsURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)
                val dataObject = jsonObject.getJSONObject("data")
                
                val rates = mutableMapOf<String, Double>()
                val currencies = listOf("PLN", "USD", "EUR", "GBP", "CZK")
                currencies.forEach { currency ->
                    if (dataObject.has(currency)) {
                        rates[currency] = dataObject.getDouble(currency)
                    }
                }
                rates
            } else {
                println("DEBUG: API Error: ${connection.responseCode}")
                emptyMap()
            }
        } catch (e: Exception) {
            println("DEBUG: Network Error when fetching rates: ${e.localizedMessage}")
            emptyMap()
        }
    }
    
    /**
     * Fetch conversion rate for selected currency
     */
    fun fetchConversionRate() {
        viewModelScope.launch {
            try {
                val fromCurrency = currency.value ?: "PLN"
                val rates = withContext(Dispatchers.IO) {
                    fetchConversionRates(fromCurrency)
                }
                
                // For conversion rate, we need PLN rate
                val rate = rates["PLN"] ?: when (fromCurrency) {
                    "USD" -> 4.0
                    "EUR" -> 4.35
                    "GBP" -> 5.0
                    "CZK" -> 0.175
                    else -> 1.0 // Default to no conversion
                }
                
                conversionRate.postValue(rate)
                println("DEBUG: Conversion rate set: $fromCurrency to PLN = $rate")
            } catch (e: Exception) {
                println("DEBUG: Error fetching conversion rate: ${e.localizedMessage}")
                // Use fallback rates
                val rate = when (currency.value) {
                    "USD" -> 4.0
                    "EUR" -> 4.35
                    "GBP" -> 5.0
                    "CZK" -> 0.175
                    else -> 1.0 // Default to no conversion
                }
                conversionRate.postValue(rate)
            }
        }
    }
    
    /**
     * Reset the state of the ViewModel (clear errors, empty fields)
     */
    fun resetState() {
        amount.value = ""
        pays.value = ""
        indebted.value = ""
        hasError.value = false
        errorMessage.value = ""
        successMessage.value = ""
    }
    
    /**
     * Load friends with their emails
     * This is public to allow refresh from the UI
     */
    fun loadFriendsWithEmails() {
        viewModelScope.launch {
            try {
                isLoadingFriends.postValue(true)
                val friendIds = loginViewModel.getFriendsList()
                val friendPairs = friendIds.map { id ->
                    val email = loginViewModel.fetchUserEmail(id)
                    id to email
                }
                // Add current user to the list
                val currentUser = loginViewModel.currentUser.value
                val currentUserEmail = currentUser?.email ?: "Me"
                val currentUserUID = currentUser?.uid ?: ""

                val allUsersForSelection = mutableListOf<Pair<String, String>>()
                if (currentUserUID.isNotEmpty()) {
                    allUsersForSelection.add(currentUserUID to "$currentUserEmail (Me)")
                }
                allUsersForSelection.addAll(friendPairs)

                println("DEBUG: Loaded ${allUsersForSelection.size} users for transaction screen (including self)")
                friendsWithEmails.postValue(allUsersForSelection)
                isLoadingFriends.postValue(false)
            } catch (e: Exception) {
                println("DEBUG: Error loading friends: ${e.localizedMessage}")
                hasError.postValue(true)
                errorMessage.postValue("Error loading friends: ${e.localizedMessage}")
                isLoadingFriends.postValue(false)
            }
        }
    }
}