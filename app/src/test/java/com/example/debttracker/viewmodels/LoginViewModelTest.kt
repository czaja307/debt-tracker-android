package com.example.debttracker.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.debttracker.models.FirestoreUser
import com.example.debttracker.models.Transaction
import com.example.debttracker.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.util.Date
import kotlinx.coroutines.test.runTest // Import for runTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    // This rule swaps the background executor used by the Architecture Components with a different one
    // that executes each task synchronously.
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: LoginViewModel

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockDb: FirebaseFirestore

    @Mock
    private lateinit var mockAuthResult: AuthResult

    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser

    @Mock
    private lateinit var mockCollectionReference: com.google.firebase.firestore.CollectionReference

    @Mock
    private lateinit var mockDocumentReference: com.google.firebase.firestore.DocumentReference

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(mockAuth, mockDb)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original one
    }

    @Test
    fun calculateBalance_correctlyCalculatesNetBalance() {
        // Arrange
        val currentUserUid = "currentUserUid"
        val friendUid = "friendUid"

        val transactions = listOf(
            Transaction(
                amount = 100.0,
                date = Date(),
                paidBy = currentUserUid
            ), // Current user paid 100
            Transaction(amount = 50.0, date = Date(), paidBy = friendUid),      // Friend paid 50
            Transaction(
                amount = 25.0,
                date = Date(),
                paidBy = currentUserUid
            )  // Current user paid 25
        )

        // Directly set LiveData values for testing this specific method.
        // In a real scenario with a private _currentUser and _storedUser, this would be harder.
        // We might need to expose setters for testing or refactor how User and FirestoreUser are loaded.
        val mockCurrentUser = User(currentUserUid, "current@test.com")
        val mockStoredUser = FirestoreUser(
            uid = currentUserUid,
            email = "current@test.com",
            name = "Current User",
            transactions = mapOf(friendUid to transactions)
        )

        // Simulate LiveData being populated as if the user is logged in and data is fetched.
        // This is a simplified way to set the internal state for this test.
        // Accessing private members like this is generally not recommended for production code
        // but can be a pragmatic approach in tests if direct setters aren't available.
        val currentUserField = LoginViewModel::class.java.getDeclaredField("_currentUser")
        currentUserField.isAccessible = true
        (currentUserField.get(viewModel) as MutableLiveData<User?>).postValue(mockCurrentUser)

        val storedUserField = LoginViewModel::class.java.getDeclaredField("_storedUser")
        storedUserField.isAccessible = true
        (storedUserField.get(viewModel) as MutableLiveData<FirestoreUser?>).postValue(mockStoredUser)


        // Act
        val balance = viewModel.calculateBalance(friendUid)

        // Assert
        // Expected: (100 + 25) - 50 = 75
        assertEquals(75.0, balance, 0.001)
    }

    @Test
    fun calculateBalance_returnsZero_whenNoTransactionsExist() {
        // Arrange
        val currentUserUid = "currentUserUid"
        val friendUid = "friendUid"

        val mockCurrentUser = User(currentUserUid, "current@test.com")
        val mockStoredUser = FirestoreUser(
            uid = currentUserUid,
            email = "current@test.com",
            name = "Current User",
            transactions = emptyMap() // No transactions for any friend
        )
        val currentUserField = LoginViewModel::class.java.getDeclaredField("_currentUser")
        currentUserField.isAccessible = true
        (currentUserField.get(viewModel) as MutableLiveData<User?>).postValue(mockCurrentUser)

        val storedUserField = LoginViewModel::class.java.getDeclaredField("_storedUser")
        storedUserField.isAccessible = true
        (storedUserField.get(viewModel) as MutableLiveData<FirestoreUser?>).postValue(mockStoredUser)

        // Act
        val balance = viewModel.calculateBalance(friendUid)

        // Assert
        assertEquals(0.0, balance, 0.001)
    }

    @Test
    fun calculateBalance_returnsZero_whenNoTransactionsForSpecificFriend() {
        // Arrange
        val currentUserUid = "currentUserUid"
        val friendUid = "friendUid"
        val otherFriendUid = "otherFriendUid"

        val transactionsWithOtherFriend = listOf(
            Transaction(amount = 100.0, date = Date(), paidBy = currentUserUid)
        )
        val mockCurrentUser = User(currentUserUid, "current@test.com")
        val mockStoredUser = FirestoreUser(
            uid = currentUserUid,
            email = "current@test.com",
            name = "Current User",
            transactions = mapOf(otherFriendUid to transactionsWithOtherFriend) // Transactions exist, but not for 'friendUid'
        )

        val currentUserField = LoginViewModel::class.java.getDeclaredField("_currentUser")
        currentUserField.isAccessible = true
        (currentUserField.get(viewModel) as MutableLiveData<User?>).postValue(mockCurrentUser)

        val storedUserField = LoginViewModel::class.java.getDeclaredField("_storedUser")
        storedUserField.isAccessible = true
        (storedUserField.get(viewModel) as MutableLiveData<FirestoreUser?>).postValue(mockStoredUser)
        // Act
        val balance = viewModel.calculateBalance(friendUid)

        // Assert
        assertEquals(0.0, balance, 0.001)
    }

    @Test
    fun signIn_success() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        viewModel.email.value = email
        viewModel.password.value = password

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(mockAuthResult)) // Mock Task to return AuthResult
        whenever(mockAuthResult.user).thenReturn(mockFirebaseUser) // mockAuthResult returns mockFirebaseUser
        whenever(mockFirebaseUser.uid).thenReturn("testUid")
        whenever(mockFirebaseUser.email).thenReturn(email)

        // Act
        viewModel.signIn()
        testDispatcher.scheduler.advanceUntilIdle() // Execute pending coroutines

        // Assert
        assertEquals(false, viewModel.hasError.value)
        // LoginViewModel's authStateListener should trigger _currentUser and isLoggedIn updates.
        // We need to mock the addAuthStateListener and invoke it.
        // For simplicity here, we check that no error was posted.
        // A more complete test would verify LiveData changes after listener invocation.
    }

    @Test
    fun signIn_failure() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        viewModel.email.value = email
        viewModel.password.value = password
        val exceptionMessage = "Sign in failed"

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forException(Exception(exceptionMessage)))

        // Act
        viewModel.signIn()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(true, viewModel.hasError.value)
        assertEquals(exceptionMessage, viewModel.errorMessage.value)
    }

    @Test
    fun signUp_success() = runTest {
        // Arrange
        val email = "new@example.com"
        val password = "newpassword"
        viewModel.email.value = email
        viewModel.password.value = password

        whenever(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(mockAuthResult))
        whenever(mockAuthResult.user).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.uid).thenReturn("newUid")
        whenever(mockFirebaseUser.email).thenReturn(email)

        // Mock Firestore operations for createUserInFirestore
        // val mockDocumentReference = mock<com.google.firebase.firestore.DocumentReference>() // Already a class member
        whenever(mockDb.collection("users")).thenReturn(mockCollectionReference) // Mock collection first
        whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference) // Then mock document on the collection
        whenever(mockDocumentReference.set(any()))
            .thenReturn(Tasks.forResult(null)) // Mock successful set

        // Act
        viewModel.signUp()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(false, viewModel.hasError.value)
        // Similar to signIn, a complete test would verify LiveData changes and Firestore interactions.
    }

    @Test
    fun signUp_failure_auth() = runTest {
        // Arrange
        val email = "new@example.com"
        val password = "newpassword"
        viewModel.email.value = email
        viewModel.password.value = password
        val exceptionMessage = "Auth creation failed"

        whenever(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forException(Exception(exceptionMessage)))

        // Act
        viewModel.signUp()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(true, viewModel.hasError.value)
        assertEquals(exceptionMessage, viewModel.errorMessage.value)
    }

    @Test
    fun signOut_executes() = runTest {
        // Arrange
        // No specific arrangement needed other than mockAuth being available.
        // We are just checking if auth.signOut() is called.

        // Act
        viewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(mockAuth).signOut()
        assertEquals(
            false,
            viewModel.hasError.value
        ) // Assuming signOut itself doesn't throw an error directly in this path
    }
}