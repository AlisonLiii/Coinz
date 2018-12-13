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
import static android.support.test.espresso.Espresso.pressBack;
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
public class ShowPropertyTest {
    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);
    @Rule
    public GrantPermissionRule grantPermissionRule=GrantPermissionRule .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


    /*To test three things:
    1) whether after clicking on the navigation drawer, the property page can be displayed
    2) whether user can go to BankCoinzActivity after clicking on the button "go banking!"
    3) whether user can go back to the map in the BankCoinzActivity after clicking the button "Back to Map"
    */

    //if the emulator asks to update google play service, please make sure to test after updating!
//Sometimes the test will fail because the emulator is working unexpectedly slow and therefore the sleeping time interval is too short..
//Just modify the sleeping interval to pass the test
    @Test
    public void showPropertyTest() {
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
                        2),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


 //To check whether the property fragment has been displayed
        onView(withText("My Properties Information")).check(matches(isDisplayed()));

        //to check whether user can go banking after clicking the button in the fragment
        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.put_coin_in_bank), withText("Go Banking!"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_property),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                12),
                        isDisplayed()));
        appCompatButton4.perform(click());

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Bank")).check(matches(isDisplayed()));



        //to check whether user can go back to the mapview after clicking the button in bankcoinzactivity
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.bank_to_main), withText("Back to Map"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar_bank),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        onView(withId(R.id.mapView)).check(matches(isDisplayed()));

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
