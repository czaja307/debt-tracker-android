package com.example.debttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.debttracker.models.FirestoreUser
import com.example.debttracker.models.Transaction
import com.example.debttracker.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class LoginViewModel : ViewModel() {
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

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            _currentUser.value = User(user.uid, user.email ?: "")
            isLoggedIn.postValue(true)
            fetchUser()
        } else {
            _currentUser.postValue(null)
            _storedUser.postValue(null)
            isLoggedIn.postValue(null)
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
        db.collection("users").document(current.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    val userData = document.data!!
                    val firestoreUser = FirestoreUser.fromMap(userData)
                    if (firestoreUser != null) {
                        _storedUser.postValue(firestoreUser)
                    } else {
                        hasError.postValue(true)
                        errorMessage.postValue("Error while fetching user data")
                    }
                } else {
                    hasError.postValue(true)
                    errorMessage.postValue("User not found")
                }
            }
            .addOnFailureListener { exception ->
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

                userRef.update("transactions.$friendUID", FieldValue.arrayUnion(transaction.toMap())).await()
                friendRef.update("transactions.$current", FieldValue.arrayUnion(transaction.toMap())).await()

                _storedUser.value?.let { user ->
                    val updatedTransactions = user.transactions.toMutableMap()
                    val currentList = updatedTransactions[friendUID]?.toMutableList() ?: mutableListOf()
                    currentList.add(transaction)
                    updatedTransactions[friendUID] = currentList
                    _storedUser.postValue(user.copy(transactions = updatedTransactions))
                }
            } catch (e: Exception) {
                hasError.postValue(true)
                errorMessage.postValue(e.localizedMessage ?: "Error while adding transaction")
            }
        }
    }

    fun calculateBalance(friendUID: String): Double {
        val transactions = _storedUser.value?.transactions?.get(friendUID) ?: emptyList()
        var balance = 0.0
        val currentUserUID = _currentUser.value?.uid ?: return 0.0

        transactions.forEach { transaction ->
            if (transaction.paidBy == currentUserUID) {
                balance += transaction.amount
            } else {
                balance -= transaction.amount
            }
        }
        return balance
    }


}






