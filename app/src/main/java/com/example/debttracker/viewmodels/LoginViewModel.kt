package com.example.debttracker.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.debttracker.data.PreferencesManager
import com.example.debttracker.models.FirestoreUser
import com.example.debttracker.models.Transaction
import com.example.debttracker.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class LoginViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val context: Context? = null
) : ViewModel() {
    
    // Initialize PreferencesManager only if context is provided
    private val preferencesManager by lazy { context?.let { PreferencesManager(it) } }
    private val apiKey: String = "fca_live_9r3DTzOKWo8YyvDndrpNu9Rl2rELohMD3VuxJBOj"
    private val conversionRates = MutableLiveData<Map<String, Double>>(emptyMap())
    val email = MutableLiveData("email@p1.pl")
    val password = MutableLiveData("123456")
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    private val _storedUser = MutableLiveData<FirestoreUser?>()
    val storedUser: LiveData<FirestoreUser?> = _storedUser

    val hasError = MutableLiveData(false)
    val errorMessage = MutableLiveData("")
    val isLoggedIn = MutableLiveData(false)
    val showSignupView = MutableLiveData(false)
    val totalBalance = MutableLiveData(0.0)
    val friendEmail = MutableLiveData("")

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            _currentUser.value = User(user.uid, user.email ?: "")
            isLoggedIn.postValue(true)
            fetchUser()
        } else {
            _currentUser.postValue(null)
            _storedUser.postValue(null)
            isLoggedIn.postValue(false)
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun signIn() {
        viewModelScope.launch {
            try {
                hasError.postValue(false)
                auth.signInWithEmailAndPassword(email.value!!, password.value!!).await()
            } catch (e: Exception) {
                hasError.postValue(true)
                errorMessage.postValue(e.localizedMessage ?: "Error while logging in")
            }
        }
    }

    fun signUp() {
        viewModelScope.launch {
            try {
                hasError.postValue(false)
                auth.createUserWithEmailAndPassword(email.value!!, password.value!!).await()
                createUserInFirestore()
            } catch (e: Exception) {
                hasError.postValue(true)
                errorMessage.postValue(e.localizedMessage ?: "Error while signing in")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                hasError.postValue(false)
                auth.signOut()
            } catch (e: Exception) {
                hasError.postValue(true)
                errorMessage.postValue(e.localizedMessage ?: "Error while signing out")
            }
        }
    }

    private fun fetchUser() {
        val current = _currentUser.value ?: return
        println("DEBUG: fetchUser called for user: ${current.uid}")
        db.collection("users").document(current.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    val userData = document.data!!
                    println("DEBUG: User data retrieved from Firestore")
                    val firestoreUser = FirestoreUser.fromMap(userData)
                    if (firestoreUser != null) {
                        println("DEBUG: FirestoreUser created successfully")
                        println("DEBUG: Friends list: ${firestoreUser.friends}")
                        println("DEBUG: Friends count: ${firestoreUser.friends.size}")
                        _storedUser.postValue(firestoreUser)
                    } else {
                        println("DEBUG: Failed to create FirestoreUser from data")
                        hasError.postValue(true)
                        errorMessage.postValue("Error while fetching user data")
                    }
                } else {
                    println("DEBUG: Document or document data is null")
                    hasError.postValue(true)
                    errorMessage.postValue("User not found")
                }
            }
            .addOnFailureListener { exception ->
                println("DEBUG: Error fetching user: ${exception.message}")
                hasError.postValue(true)
                errorMessage.postValue(
                    exception.localizedMessage ?: "Error while fetching user data"
                )
            }
    }

    private fun createUserInFirestore() {
        val current = _currentUser.value ?: return
        val userRef = db.collection("users")
            .document(current.uid)
        val data = mapOf(
            "uid" to current.uid,
            "email" to current.email,
            "name" to ""
        )
        val newUser = FirestoreUser.fromMap(data) ?: return
        userRef.set(newUser.toMap())
            .addOnFailureListener { exception ->
                hasError.postValue(true)
                errorMessage.postValue(
                    exception.localizedMessage ?: "Error while creating user in Firestore"
                )
            }
    }

    fun sendFriendRequest(toEmail: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("users")
                    .whereEqualTo("email", toEmail)
                    .get()
                    .await()
                if (querySnapshot.documents.isEmpty()) {
                    hasError.postValue(true)
                    errorMessage.postValue("User not found")
                    return@launch
                }

                val friendDoc = querySnapshot.documents.first()
                val friendData = friendDoc.data ?: run {
                    hasError.postValue(true)
                    errorMessage.postValue("User not found")
                    return@launch
                }
                val friend = FirestoreUser.fromMap(friendData) ?: run {
                    hasError.postValue(true)
                    errorMessage.postValue("A parsing error occurred")
                    return@launch
                }
                val current = _currentUser.value?.uid ?: return@launch
                val currentUserRef = db.collection("users").document(current)
                val friendRef = db.collection("users").document(friend.uid)

                currentUserRef.update("outgoingRequests", FieldValue.arrayUnion(friend.uid)).await()
                friendRef.update("incomingRequests", FieldValue.arrayUnion(current)).await()

                _storedUser.value?.let { user ->
                    val updatedOutgoingRequests =
                        user.outgoingRequests.toMutableList().apply { add(friend.uid) }
                    _storedUser.postValue(user.copy(outgoingRequests = updatedOutgoingRequests))
                }
            } catch (e: Exception) {
                hasError.postValue(true)
                errorMessage.postValue(e.localizedMessage ?: "Error while sending friend request")
            }
        }
    }

    fun acceptFriendRequest(fromUID: String) {
        viewModelScope.launch {
            try {
                val current = _currentUser.value?.uid ?: return@launch
                val currentUserRef = db.collection("users").document(current)
                val friendRef = db.collection("users").document(fromUID)

                val friendSnapshot = friendRef.get().await()
                val friendData = friendSnapshot.data ?: run {
                    hasError.postValue(true)
                    errorMessage.postValue("User not found")
                    return@launch
                }
                val friend = FirestoreUser.fromMap(friendData) ?: run {
                    hasError.postValue(true)
                    errorMessage.postValue("A parsing error occurred")
                    return@launch
                }

                currentUserRef.update(
                    "friends", FieldValue.arrayUnion(fromUID),
                    "incomingRequests", FieldValue.arrayRemove(fromUID)
                ).await()

                friendRef.update(
                    "friends", FieldValue.arrayUnion(current),
                    "outgoingRequests", FieldValue.arrayRemove(current)
                ).await()

                _storedUser.value?.let { user ->
                    val updatedFriends = user.friends.toMutableList().apply { add(friend.uid) }
                    val updatedIncomingRequests = user.incomingRequests.filter { it != fromUID }
                    _storedUser.postValue(
                        user.copy(
                            friends = updatedFriends,
                            incomingRequests = updatedIncomingRequests
                        )
                    )
                }
            } catch (e: Exception) {
                hasError.postValue(true)
                errorMessage.postValue(e.localizedMessage ?: "Error while accepting friend request")

            }
        }
    }

    suspend fun fetchUserEmail(forUID: String): String {
        return try {
            val snapshot = db.collection("users").document(forUID).get().await()
            snapshot.data?.get("email") as? String ?: forUID
        } catch (e: Exception) {
            forUID
        }
    }

    fun addTransaction(friendUID: String, amount: Double, paidBy: String) {
        viewModelScope.launch {
            try {
                val current = _currentUser.value?.uid ?: return@launch
                val userRef = db.collection("users").document(current)
                val friendRef = db.collection("users").document(friendUID)
                val data = mapOf(
                    "amount" to amount,
                    "date" to Timestamp(Date()),
                    "paidBy" to paidBy
                )
                val transaction = Transaction.fromMap(data) ?: return@launch

                userRef.update(
                    "transactions.$friendUID",
                    FieldValue.arrayUnion(transaction.toMap())
                ).await()
                friendRef.update(
                    "transactions.$current",
                    FieldValue.arrayUnion(transaction.toMap())
                ).await()

                _storedUser.value?.let { user ->
                    val updatedTransactions = user.transactions.toMutableMap()
                    val currentList =
                        updatedTransactions[friendUID]?.toMutableList() ?: mutableListOf()
                    currentList.add(transaction)
                    // Sort by date descending (newest first)
                    val sortedList = currentList.sortedByDescending { it.date.time }
                    updatedTransactions[friendUID] = sortedList
                    _storedUser.postValue(user.copy(transactions = updatedTransactions))
                }
            } catch (e: Exception) {
                hasError.postValue(true)
                errorMessage.postValue(e.localizedMessage ?: "Error while adding transaction")
            }
        }
    }

    /**
     * Calculates the balance between the current user and a friend
     * Converts the balance to the user's preferred currency
     */
    fun calculateBalance(friendUID: String): Double {
        // Get transactions and essential data
        val transactions = _storedUser.value?.transactions?.get(friendUID) ?: emptyList()
        val currentUserUID = _currentUser.value?.uid ?: return 0.0

        // Calculate balance in PLN (base currency for stored data)
        var balancePLN = 0.0
        transactions.forEach { transaction ->
            if (transaction.paidBy == currentUserUID) {
                balancePLN += transaction.amount
            } else {
                balancePLN -= transaction.amount
            }
        }
        
        // If no context, return balance in PLN
        if (context == null) {
            return balancePLN
        }
        
        // Convert to user's preferred currency if needed
        return try {
            // Use safe call with let to avoid smart cast issue
            val preferredCurrency = preferencesManager?.let {
                runBlocking { it.userCurrency.first() }
            } ?: "PLN"
            
            // Return the balance in preferred currency
            convertCurrency(balancePLN, "PLN", preferredCurrency)
        } catch (e: Exception) {
            // If anything goes wrong, return the original PLN balance
            println("DEBUG: Error converting balance: ${e.localizedMessage}")
            balancePLN
        }
    }

    fun refreshUserData() {
        viewModelScope.launch {
            try {
                println("DEBUG: Refreshing user data...")
                
                // Get preferred currency if context is available
                var preferredCurrency = "PLN" // Default fallback
                
                // Using safe calls with let to avoid smart cast issue
                context?.let { ctx ->
                    preferencesManager?.let { prefs ->
                        preferredCurrency = prefs.userCurrency.first()
                        
                        // Fetch conversion rates for the user's preferred currency 
                        // This will be used for all balance calculations
                        val rates = withContext(Dispatchers.IO) {
                            fetchConversionRates(preferredCurrency)
                        }
                        println("DEBUG: Fetched conversion rates for $preferredCurrency: $rates")
                        conversionRates.postValue(rates)
                    }
                }
                
                _currentUser.value?.let { user ->
                    val uid = user.uid
                    println("DEBUG: Fetching data for user with UID: $uid")
                    val userDoc = db.collection("users").document(uid).get().await()
                    userDoc?.let {
                        val data = it.data
                        data?.let { userData ->
                            println("DEBUG: User data fetched, processing...")
                            val firestoreUser = FirestoreUser.fromMap(userData)
                            firestoreUser?.let { userWithData ->
                                println("DEBUG: FirestoreUser created, friends count: ${userWithData.friends.size}")
                                println("DEBUG: Friends list: ${userWithData.friends}")
                                _storedUser.postValue(userWithData)
                                
                                // Calculate total balance in PLN
                                var totalPLN = 0.0
                                userWithData.friends.forEach { friendId ->
                                    // Use calculateBalance which already handles the direction correctly
                                    // but returns in the preferred currency, so we need the PLN version
                                    val friendTransactions = userWithData.transactions[friendId] ?: emptyList()
                                    var friendBalance = 0.0
                                    
                                    // Direct calculation in PLN
                                    friendTransactions.forEach { transaction ->
                                        if (transaction.paidBy == uid) {
                                            friendBalance += transaction.amount
                                        } else {
                                            friendBalance -= transaction.amount
                                        }
                                    }
                                    
                                    totalPLN += friendBalance
                                }
                                
                                // Convert total balance to preferred currency
                                val convertedTotal = convertCurrency(totalPLN, "PLN", preferredCurrency)
                                totalBalance.postValue(convertedTotal)
                                println("DEBUG: Total balance: $totalPLN PLN -> $convertedTotal $preferredCurrency")
                                
                            } ?: println("DEBUG: Failed to create FirestoreUser from data")
                        } ?: println("DEBUG: User document data is null")
                    } ?: println("DEBUG: User document is null")
                } ?: println("DEBUG: Current user is null")
            } catch (e: Exception) {
                println("DEBUG: Error refreshing user data: ${e.message}")
                hasError.postValue(true)
                errorMessage.postValue("Failed to refresh user data: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Suspend helper to retrieve friend IDs from Firestore for current user
     */
    suspend fun getFriendsList(): List<String> {
        val uid = _currentUser.value?.uid ?: return emptyList()
        val doc = db.collection("users").document(uid).get().await()
        val data = doc.data
        return (data?.get("friends") as? List<String>) ?: emptyList()
    }

    /**
     * Fetches currency conversion rates from the API for all available currencies
     * Used to convert transactions to the user's preferred currency
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
     * Converts an amount from one currency to another using cached rates
     * If rates are not available, it falls back to the simplified conversion
     */
    private fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String): Double {
        // If same currency, no conversion needed
        if (fromCurrency == toCurrency) return amount

        val actualPreferredCurrency = preferencesManager?.let {
            // This runBlocking call can be problematic if convertCurrency is called frequently on the main thread.
            // Consider refactoring how preferredCurrency is passed or how conversion is triggered if performance issues arise.
            runBlocking { it.userCurrency.first() }
        } ?: "PLN"
        
        val rates = conversionRates.value ?: emptyMap() // these rates have actualPreferredCurrency as base

        // Check if API rates are available and relevant for PLN <-> actualPreferredCurrency conversion
        if (rates.isNotEmpty() && rates.containsKey("PLN")) {
            val plnPerPreferredRate = rates["PLN"]!! // How many PLN for 1 unit of actualPreferredCurrency

            if (fromCurrency == "PLN" && toCurrency == actualPreferredCurrency) {
                // Convert amount (in PLN) to actualPreferredCurrency
                if (plnPerPreferredRate == 0.0) {
                    println("DEBUG: PLN per Preferred Rate is 0, cannot divide. Falling back.")
                    // Fallback to hardcoded or return original amount to avoid crash
                } else {
                    return amount / plnPerPreferredRate
                }
            } else if (fromCurrency == actualPreferredCurrency && toCurrency == "PLN") {
                // Convert amount (in actualPreferredCurrency) to PLN
                return amount * plnPerPreferredRate
            }
        }
        
        // Fall back to simplified hardcoded conversion if API rates not available or specific path not met
        println("DEBUG: Falling back to hardcoded conversion for $fromCurrency to $toCurrency")
        return when {
            fromCurrency == "PLN" && toCurrency == "USD" -> amount * 0.25  // 4 PLN = 1 USD
            fromCurrency == "PLN" && toCurrency == "EUR" -> amount * 0.23  // 4.35 PLN = 1 EUR
            fromCurrency == "PLN" && toCurrency == "GBP" -> amount * 0.20  // 5 PLN = 1 GBP
            fromCurrency == "PLN" && toCurrency == "CZK" -> amount * 5.70  // 1 PLN = 5.7 CZK
            toCurrency == "PLN" && fromCurrency == "USD" -> amount * 4.0   // 1 USD = 4 PLN
            toCurrency == "PLN" && fromCurrency == "EUR" -> amount * 4.35  // 1 EUR = 4.35 PLN
            toCurrency == "PLN" && fromCurrency == "GBP" -> amount * 5.0   // 1 GBP = 5 PLN
            toCurrency == "PLN" && fromCurrency == "CZK" -> amount / 5.7   // 5.7 CZK = 1 PLN
            else -> amount // Unknown conversion or already handled (e.g. PLN to PLN), return original
        }
    }
}






