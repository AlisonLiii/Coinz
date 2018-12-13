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
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public class ChatTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);
    @Rule
    public GrantPermissionRule grantPermissionRule=GrantPermissionRule .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    /*ONE TEST:
    To test :
    1)whether registered players are displayed to the user in the People Fragment
    2)whether message can be sent from users
    3)whether user can go to ShareCoinzActivity after clicking on the lower left button on the chat page
     */
    //if the emulator asks to update google play service, please make sure to test after updating!
//Sometimes the test will fail because the emulator is working unexpectedly slow and therefore the sleeping time interval is too short..
//Just modify the sleeping interval to pass the test
    @Test
    public void chatTest() {
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
                        3),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //check whether registered user has been displayed on the page
        ViewInteraction relativeLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.cardView_item_person),
                                childAtPosition(
                                        withId(R.id.recycler_view_people),
                                        0)),
                        0),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view_people),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                3)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //to send the message
        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.editText_message),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout_message),
                                        childAtPosition(
                                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        textInputEditText.perform(replaceText("Hi there hey"), closeSoftKeyboard());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //to send the message
        onView(withId(R.id.imageView_send)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        //to check whether the user can go to ShareCoinzActivity after clicking on the lower left button
        onView(withId(R.id.fab_send_coin)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //if the user has been successfully directed to ShareCoinzActivity, the below text will be displayed
        ViewInteraction textView2 = onView(
                allOf(withText("Coinz you collect yourself in your wallet"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar_share),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.view.ViewGroup.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("Coinz you collect yourself in your wallet")));


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
