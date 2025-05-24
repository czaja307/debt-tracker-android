# DebtTracker

## Test Summary

### Unit Tests (`app/src/test/java/`)

#### `DataModelsTest.kt` (Data Model Integrity)

| Test Case                                           | Description                                                                                                                                        |
|:----------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------|
| `transaction_fromMap_and_toMap_validData`           | Verifies `Transaction.fromMap` and `Transaction.toMap` work correctly with valid data.                                                             |
| `transaction_fromMap_missingAmount`                 | Checks that `Transaction.fromMap` returns `null` if the 'amount' field is missing.                                                                 |
| `transaction_fromMap_missingDate`                   | Checks that `Transaction.fromMap` returns `null` if the 'date' field is missing.                                                                   |
| `transaction_fromMap_missingPaidBy`                 | Checks that `Transaction.fromMap` returns `null` if the 'paidBy' field is missing.                                                                 |
| `transaction_fromMap_invalidAmountType`             | Ensures `Transaction.fromMap` returns `null` if 'amount' has an invalid data type.                                                                 |
| `user_equality`                                     | Tests the equality logic for the `User` data class based on `uid` and `email`.                                                                     |
| `firestoreUser_fromMap_and_toMap_validData`         | Verifies `FirestoreUser.fromMap` and `FirestoreUser.toMap` work correctly with all fields, including nested transactions.                          |
| `firestoreUser_fromMap_missingOptionalFields`       | Checks `FirestoreUser.fromMap` correctly handles missing optional fields (e.g., friends, requests, transactions), defaulting to empty collections. |
| `firestoreUser_fromMap_emptyListsForOptionalFields` | Ensures `FirestoreUser.fromMap` correctly handles explicitly empty lists for optional fields.                                                      |
| `firestoreUser_fromMap_missingUid`                  | Checks that `FirestoreUser.fromMap` returns `null` if the mandatory 'uid' field is missing.                                                        |

#### `LoginViewModelTest.kt` (ViewModel Logic)

| Test Case                                                          | Description                                                                                                                                                                                  |
|:-------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `calculateBalance_correctlyCalculatesNetBalance`                   | Verifies that the balance between the current user and a friend is calculated correctly based on a list of transactions.                                                                     |
| `calculateBalance_returnsZero_whenNoTransactionsExist`             | Ensures balance is zero if there are no transactions at all for the current user.                                                                                                            |
| `calculateBalance_returnsZero_whenNoTransactionsForSpecificFriend` | Ensures balance is zero for a specific friend if no transactions exist with that friend, even if transactions exist with other friends.                                                      | 
| `signIn_success`                                                   | Tests successful sign-in via Firebase, checking for no errors (basic check).                                                                                                                 |
| `signIn_success_fetchesUser`                                       | Tests successful sign-in, manual `AuthStateListener` invocation, and subsequent successful fetch of user data from Firestore. Verifies `currentUser`, `storedUser`, and `isLoggedIn` states. | 
| `signIn_failure`                                                   | Tests failed sign-in attempt, ensuring error state and message are correctly set in the ViewModel.                                                                                           |
| `signUp_success`                                                   | Tests successful user registration with Firebase Auth and subsequent user data creation in Firestore.                                                                                        |
| `signUp_failure_auth`                                              | Tests failed user registration due to an authentication error, ensuring error state and message are set.                                                                                     |
| `signOut_executes`                                                 | Verifies that the `signOut` method on `FirebaseAuth` is called when the ViewModel's signOut is invoked.                                                                                      |

### UI/Instrumentation Tests (`app/src/androidTest/java/`)

#### `AppNavigationTest.kt` (Application Navigation Flows)

| Test Case                                                           | Description                                                                                                                                 |
|:--------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------|
| `appLaunches_and_homeScreenIsVisible`                               | Verifies that the application launches correctly and the `HomeScreen` (identified by "You owe" text) is displayed.                          |
| `navigateToFriendsScreen_fromBottomNav`                             | Tests navigation from the `HomeScreen` to the `FriendsScreen` using the bottom navigation bar.                                              |
| `navigateToProfileSettings_fromTopAppBar_showsLoginScreenInitially` | Tests navigation to the profile/settings area (which shows `LoginScreen` if not logged in) by clicking the profile icon in the top app bar. |
| `navigateToNewTransactionScreen_fromBottomNav`                      | Tests navigation from the `HomeScreen` to the `NewTransactionScreen` using the "New Transaction" button in the bottom navigation bar.       |

#### `PerformanceTest.java` (API and Database Performance)

| Test Case                                 | Description                                                                                                                                              |
|:------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `testCurrencyApiResponseTime`             | Tests the response time of the FreeCurrencyAPI used for currency conversion, ensuring it responds within 5 seconds with valid exchange rate data.      |
| `testFirestoreConnectionPerformance`      | Verifies Firestore database connection performance by executing a simple query and ensuring response time is under 3 seconds.                          |
| `testCurrencyApiDataValidation`          | Validates that the currency API returns properly formatted data with reasonable exchange rates (PLN rate between 0.1 and 10 relative to USD).          |
| `testFirebaseAuthPerformance`            | Tests Firebase Authentication initialization performance to ensure it completes within 5 seconds.                                                        |
| `testMultipleCurrencyApiCalls`           | Tests performance of consecutive currency API calls for different currencies (EUR, GBP, CZK) with average response time under 4 seconds.               |
| `testFirestoreWritePerformance`          | Measures Firestore write operation performance by creating and deleting a test document, ensuring completion within 4 seconds.                         |
| `testCurrencyApiErrorHandling`           | Tests API error handling by using an invalid API key and verifying that the system properly handles authentication failures.                            |
| `testNetworkTimeoutHandling`             | Tests network timeout handling by setting aggressive timeout values and ensuring the system responds appropriately within 6 seconds.                   |


