package com.example.debttracker.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.debttracker.MainActivity
import com.example.debttracker.navigation.Screen
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AppNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun aa_loginScreen_performsLoginSuccessfully() {
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed()

        composeTestRule.onNodeWithText("Log In").performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithText("You owe").assertIsDisplayed()
    }

    @Test
    fun appLaunches_and_homeScreenIsVisible() {
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("You owe").assertIsDisplayed()
        composeTestRule.onNodeWithText(Screen.Home.title).assertIsDisplayed()
    }

    @Test
    fun navigateToFriendsScreen_fromBottomNav() {
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("You owe").assertIsDisplayed()

        composeTestRule.onNodeWithText(Screen.Friends.title).performClick()

        composeTestRule.onNodeWithText("Add Friend").assertIsDisplayed()
        composeTestRule.onNodeWithText("Invitations").assertIsDisplayed()
    }

    @Test
    fun navigateToNewTransactionScreen_fromBottomNav() {
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("You owe").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription(
            Screen.NewTransaction.title,
            useUnmergedTree = true
        ).performClick()

        composeTestRule.onNodeWithText("Add a new transaction").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter an amount").assertIsDisplayed()
    }
}
