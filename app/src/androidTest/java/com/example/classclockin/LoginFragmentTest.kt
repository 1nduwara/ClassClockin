package com.example.classclockin

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun login_test() {
        val email = "induwara@gmail.com"
        val password = "induwara123"
        onView(withId(R.id.txt_username)).perform(typeText(email))
        onView(withId(R.id.txt_pwd)).perform(typeText(password))
        onView(withId(R.id.btn_login)).perform(click())
    }
}