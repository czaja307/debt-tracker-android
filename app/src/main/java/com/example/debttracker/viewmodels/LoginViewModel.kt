package com.example.debttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.debttracker.models.FirestoreUser
import com.example.debttracker.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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


}


