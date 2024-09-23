package com.example.classclockin

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.classclockin.activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentUITest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testLoginScreenElementsDisplayed() {
        // Check if Username/Email field is displayed
        onView(withId(R.id.txt_username)).check(matches(isDisplayed()))

        // Check if Password field is displayed
        onView(withId(R.id.txt_pwd)).check(matches(isDisplayed()))

        // Check if Login button is displayed
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))

        // Check if Register link is displayed
        onView(withId(R.id.txt_link_reg)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginWithValidCredentials() {
        // Type a valid email or teacher ID in the username field
        onView(withId(R.id.txt_username)).perform(typeText("testuser@gmail.com"))

        // Type a valid password
        onView(withId(R.id.txt_pwd)).perform(typeText("testpassword"), closeSoftKeyboard())

        // Click the login button
        onView(withId(R.id.btn_login)).perform(click())

        // Check if home fragment is displayed (assuming login success leads to home fragment)
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginWithInvalidCredentials() {
        // Type an invalid email or teacher ID in the username field
        onView(withId(R.id.txt_username)).perform(typeText("invaliduser"))

        // Type an invalid password
        onView(withId(R.id.txt_pwd)).perform(typeText("wrongpassword"), closeSoftKeyboard())

        // Click the login button
        onView(withId(R.id.btn_login)).perform(click())

        // Check if error message is displayed (modify to match actual error message)
        onView(withText("Login failed")).check(matches(isDisplayed()))
        }
}