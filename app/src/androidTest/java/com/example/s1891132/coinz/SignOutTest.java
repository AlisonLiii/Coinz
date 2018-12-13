package com.example.s1891132.coinz;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class SignOutTest {
    //if the emulator asks to update google play service, please make sure to test after updating!
//Sometimes the test will fail because the emulator is working unexpectedly slow and therefore the sleeping time interval is too short..
//Just modify the sleeping interval to pass the test
    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);
    @Rule
    public GrantPermissionRule grantPermissionRule=GrantPermissionRule .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Test
    //To check whether user can successfully sign out and go back to log-in activity to log in
    public void signOutTest() {
        //if the user never signed in before
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            ViewInteraction appCompatEditText = onView(
                    allOf(withId(R.id.username_text),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    2),
                            isDisplayed()));
            appCompatEditText.perform(replaceText("test1@gmail.com"), closeSoftKeyboard());

            ViewInteraction appCompatEditText2 = onView(
                    allOf(withId(R.id.password_text),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    3),
                            isDisplayed()));
            appCompatEditText2.perform(replaceText("testtest"), closeSoftKeyboard());


            ViewInteraction appCompatButton = onView(
                    allOf(withId(R.id.signin), withText("Sign in"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    4),
                            isDisplayed()));
            appCompatButton.perform(click());
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navigation_view),
                                        0)),
                        5),
                        isDisplayed()));
        navigationMenuItemView.perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //if the image in the sign-in screen has displayed, user has successfully signed out and went back to log-in activity
        onView(withId(R.id.sign_in_coinz_image))
                .check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
