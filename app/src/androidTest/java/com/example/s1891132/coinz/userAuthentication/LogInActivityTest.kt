package com.example.s1891132.coinz.userAuthentication

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.example.s1891132.coinz.MainActivity
import com.example.s1891132.coinz.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class LogInActivityTest {

    @Rule
    @JvmField
    val rule  = IntentsTestRule(LogInActivity::class.java)
    val grantPermissionRule:GrantPermissionRule = GrantPermissionRule .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    private val useremail="test@gmail.com"//where to put grant permission rule
    private val correct_password ="correctpassword"
    private val wrong_password = "wrongpassword"


    @Test
    fun empty_input(){
        Log.e("@Test","Performing empty input text")

        Espresso.onView(withId(R.id.signin))
                .perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView(withText("Please enter email address and password!"))
                .check(matches(isDisplayed()))
    }

    @Test
    fun login_success(){
        Log.e("@Test","Performing login success test")
        Espresso.onView((withId(R.id.username_text)))
                .perform(ViewActions.typeText(useremail))

        Espresso.onView(withId(R.id.password_text))
                .perform(ViewActions.typeText(correct_password)).perform(ViewActions.closeSoftKeyboard())

        Espresso.onView(withId(R.id.signin))
                .perform(ViewActions.click())
        Thread.sleep(5000)
        intended(hasComponent(MainActivity::class.java.getName()))


    }

   @Test
    fun login_failure(){
        Log.e("@Test","Performing login failure test")
        Espresso.onView((withId(R.id.username_text)))
                .perform(ViewActions.typeText(useremail))

        Espresso.onView(withId(R.id.password_text))
                .perform(ViewActions.typeText(wrong_password)).perform(ViewActions.closeSoftKeyboard())

        Espresso.onView(withId(R.id.signin))
                .perform(ViewActions.click())
        Thread.sleep(500)
        Espresso.onView(withText("Invalid username or password, please try again. Make sure you have registered before."))
                .check(matches(isDisplayed()))
    }
}