package com.example.debttracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.debttracker.navigation.Screen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunches_and_homeScreenIsVisible() {
        // Check for a unique element on the HomeScreen
        // In HomeScreen, there's a CustomText with "You owe"
        composeTestRule.onNodeWithText("You owe").assertIsDisplayed()
        // And the bottom nav "Home" item should be visible
        composeTestRule.onNodeWithText(Screen.Home.title).assertIsDisplayed()
    }

    @Test
    fun navigateToFriendsScreen_fromBottomNav() {
        // 1. Ensure Home screen is initially displayed
        composeTestRule.onNodeWithText("You owe").assertIsDisplayed()

        // 2. Click on the "Friends" item in the bottom navigation bar
        composeTestRule.onNodeWithText(Screen.Friends.title).performClick()

        // 3. Verify FriendsScreen is displayed
        // FriendsScreen has "Add Friend" and "Invitations" buttons
        composeTestRule.onNodeWithText("Add Friend").assertIsDisplayed()
        composeTestRule.onNodeWithText("Invitations").assertIsDisplayed()
    }

    @Test
    fun navigateToProfileSettings_fromTopAppBar_showsLoginScreenInitially() {
        // 1. Ensure Home screen is initially displayed
        composeTestRule.onNodeWithText("You owe").assertIsDisplayed()

        // 2. Click on the profile icon in the GlobalTopAppBar
        composeTestRule.onNodeWithContentDescription("Profile").performClick()

        // 3. Verify LoginScreen is displayed (as user is not logged in by default)
        // ProfileSettingsScreen wraps LoginScreen if not logged in.
        // The LoginScreen's BackTopAppBar has title "Login".
        // We use the testTag added to the title Text in BackTopAppBar.
        composeTestRule.onNode(hasTestTag("top_app_bar_title") and hasText("Login"), useUnmergedTree = true).assertIsDisplayed()

        // Check for a unique element on the LoginScreen, e.g., the "Email" text field label
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    }

    @Test
    fun navigateToNewTransactionScreen_fromBottomNav() {
        // 1. Ensure Home screen is initially displayed
        composeTestRule.onNodeWithText("You owe").assertIsDisplayed()

        // 2. Click on the "New Transaction" item in the bottom navigation bar
        // The icon itself has contentDescription = Screen.NewTransaction.title ("New Transaction")
        composeTestRule.onNodeWithContentDescription(
            Screen.NewTransaction.title,
            useUnmergedTree = true
        ).performClick()

        // 3. Verify NewTransactionScreen is displayed
        // NewTransactionScreen has a BackTopAppBar with title "Add a new transaction"
        composeTestRule.onNodeWithText("Add a new transaction").assertIsDisplayed()
        // And a field with label "Enter an amount"
        composeTestRule.onNodeWithText("Enter an amount").assertIsDisplayed()
    }
}
