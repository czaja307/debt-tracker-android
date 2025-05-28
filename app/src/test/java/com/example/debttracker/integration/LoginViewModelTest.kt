package com.example.debttracker.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.debttracker.models.FirestoreUser
import com.example.debttracker.models.Transaction
import com.example.debttracker.models.User
import com.example.debttracker.viewmodels.LoginViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
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
import kotlinx.coroutines.test.runTest
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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import junit.framework.Assert.assertNotNull
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class LoginViewModelTest {

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
    private lateinit var mockCollectionReference: CollectionReference

    @Mock
    private lateinit var mockDocumentReference: DocumentReference

    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(mockAuth, mockDb)


        whenever(mockAuth.addAuthStateListener(any())).then {
            authStateListener = it.arguments[0] as FirebaseAuth.AuthStateListener
            null
        }

        viewModel = LoginViewModel(mockAuth, mockDb)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun calculateBalance_correctlyCalculatesNetBalance() {

        val currentUserUid = "currentUserUid"
        val friendUid = "friendUid"

        val transactions = listOf(
            Transaction(
                amount = 100.0,
                date = Date(),
                paidBy = currentUserUid
            ),
            Transaction(amount = 50.0, date = Date(), paidBy = friendUid),
            Transaction(
                amount = 25.0,
                date = Date(),
                paidBy = currentUserUid
            )
        )

        val mockCurrentUser = User(currentUserUid, "current@test.com")
        val mockStoredUser = FirestoreUser(
            uid = currentUserUid,
            email = "current@test.com",
            name = "Current User",
            transactions = mapOf(friendUid to transactions)
        )

        val currentUserField = LoginViewModel::class.java.getDeclaredField("_currentUser")
        currentUserField.isAccessible = true
        (currentUserField.get(viewModel) as MutableLiveData<User?>).postValue(mockCurrentUser)

        val storedUserField = LoginViewModel::class.java.getDeclaredField("_storedUser")
        storedUserField.isAccessible = true
        (storedUserField.get(viewModel) as MutableLiveData<FirestoreUser?>).postValue(mockStoredUser)

        val balance = viewModel.calculateBalance(friendUid)

        assertEquals(75.0, balance, 0.001)
    }

    @Test
    fun calculateBalance_returnsZero_whenNoTransactionsExist() {
        val currentUserUid = "currentUserUid"
        val friendUid = "friendUid"

        val mockCurrentUser = User(currentUserUid, "current@test.com")
        val mockStoredUser = FirestoreUser(
            uid = currentUserUid,
            email = "current@test.com",
            name = "Current User",
            transactions = emptyMap()
        )
        val currentUserField = LoginViewModel::class.java.getDeclaredField("_currentUser")
        currentUserField.isAccessible = true
        (currentUserField.get(viewModel) as MutableLiveData<User?>).postValue(mockCurrentUser)

        val storedUserField = LoginViewModel::class.java.getDeclaredField("_storedUser")
        storedUserField.isAccessible = true
        (storedUserField.get(viewModel) as MutableLiveData<FirestoreUser?>).postValue(mockStoredUser)

        val balance = viewModel.calculateBalance(friendUid)

        assertEquals(0.0, balance, 0.001)
    }

    @Test
    fun calculateBalance_returnsZero_whenNoTransactionsForSpecificFriend() {
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
            transactions = mapOf(otherFriendUid to transactionsWithOtherFriend)
        )

        val currentUserField = LoginViewModel::class.java.getDeclaredField("_currentUser")
        currentUserField.isAccessible = true
        (currentUserField.get(viewModel) as MutableLiveData<User?>).postValue(mockCurrentUser)

        val storedUserField = LoginViewModel::class.java.getDeclaredField("_storedUser")
        storedUserField.isAccessible = true
        (storedUserField.get(viewModel) as MutableLiveData<FirestoreUser?>).postValue(mockStoredUser)

        val balance = viewModel.calculateBalance(friendUid)


        assertEquals(0.0, balance, 0.001)
    }

    @Test
    fun signIn_success() = runTest {
        val email = "test@example.com"
        val password = "password123"
        viewModel.email.value = email
        viewModel.password.value = password

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(mockAuthResult))
        whenever(mockAuthResult.user).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.uid).thenReturn("testUid")
        whenever(mockFirebaseUser.email).thenReturn(email)


        viewModel.signIn()
        testDispatcher.scheduler.advanceUntilIdle()


        assertEquals(false, viewModel.hasError.value)
    }

    @Test
    fun signIn_success_fetchesUser() = runTest {

        val email = "test@example.com"
        val password = "password123"
        val uid = "testUid"
        viewModel.email.value = email
        viewModel.password.value = password

        val firestoreUserData = mapOf(
            "uid" to uid,
            "email" to email,
            "name" to "Test User",
            "friends" to emptyList<String>(),
            "incomingRequests" to emptyList<String>(),
            "outgoingRequests" to emptyList<String>(),
            "transactions" to emptyMap<String, List<Map<String, Any>>>()
        )
        val mockDocumentSnapshot = mock<DocumentSnapshot>()

        whenever(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(
            Tasks.forResult(
                mockAuthResult
            )
        )
        whenever(mockAuthResult.user).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.uid).thenReturn(uid)
        whenever(mockFirebaseUser.email).thenReturn(email)
        whenever(mockAuth.currentUser).thenReturn(mockFirebaseUser)

        val firestoreGetTask = mock<Task<DocumentSnapshot>>()
        whenever(mockDb.collection("users")).thenReturn(mockCollectionReference)
        whenever(mockCollectionReference.document(uid)).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.get()).thenReturn(firestoreGetTask)

        whenever(mockDocumentSnapshot.data).thenReturn(firestoreUserData)
        whenever(mockDocumentSnapshot.exists()).thenReturn(true)


        whenever(firestoreGetTask.addOnSuccessListener(any<OnSuccessListener<in DocumentSnapshot>>())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnSuccessListener<DocumentSnapshot>>(0)
            listener.onSuccess(mockDocumentSnapshot)
            firestoreGetTask
        }
        whenever(firestoreGetTask.addOnFailureListener(any<OnFailureListener>())).thenReturn(
            firestoreGetTask
        )

        viewModel.signIn()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull("AuthStateListener should be captured", authStateListener)

        whenever(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        authStateListener!!.onAuthStateChanged(mockAuth)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.hasError.value)
        assertEquals(User(uid, email), viewModel.currentUser.value)
        assertNotNull(viewModel.storedUser.value)
        assertEquals(uid, viewModel.storedUser.value?.uid)
        assertEquals("Test User", viewModel.storedUser.value?.name)
        assertEquals(true, viewModel.isLoggedIn.value)
    }

    @Test
    fun signIn_failure() = runTest {

        val email = "test@example.com"
        val password = "password123"
        viewModel.email.value = email
        viewModel.password.value = password
        val exceptionMessage = "Sign in failed"

        whenever(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forException(Exception(exceptionMessage)))

        viewModel.signIn()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, viewModel.hasError.value)
        assertEquals(exceptionMessage, viewModel.errorMessage.value)
    }

    @Test
    fun signUp_success() = runTest {

        val email = "new@example.com"
        val password = "newpassword"
        val newUid = "newUid"
        viewModel.email.value = email
        viewModel.password.value = password

        whenever(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(mockAuthResult))
        whenever(mockAuthResult.user).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.uid).thenReturn(newUid)
        whenever(mockFirebaseUser.email).thenReturn(email)
        whenever(mockAuth.currentUser).thenReturn(mockFirebaseUser)

        val firestoreSetTask = mock<Task<Void>>()
        whenever(mockDb.collection("users")).thenReturn(mockCollectionReference)
        whenever(mockCollectionReference.document(newUid)).thenReturn(mockDocumentReference)
        whenever(mockDocumentReference.set(any())).thenReturn(firestoreSetTask)

        whenever(firestoreSetTask.addOnFailureListener(any<OnFailureListener>())).thenAnswer { invocation ->


            firestoreSetTask
        }

        viewModel.signUp()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.hasError.value)
    }

    @Test
    fun signUp_failure_auth() = runTest {
        val email = "new@example.com"
        val password = "newpassword"
        viewModel.email.value = email
        viewModel.password.value = password
        val exceptionMessage = "Auth creation failed"

        whenever(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forException(Exception(exceptionMessage)))

        viewModel.signUp()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, viewModel.hasError.value)
        assertEquals(exceptionMessage, viewModel.errorMessage.value)
    }

    @Test
    fun signOut_executes() = runTest {
        viewModel.signOut()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(mockAuth).signOut()
        assertEquals(
            false,
            viewModel.hasError.value
        )
    }
}