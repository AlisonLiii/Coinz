package com.example.s1891132.coinz;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public class SearchFriendTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

/*Three tests here:
1) check whether user can search for an email and then go to ShareCoinzActivity to share coinz to the user with that email address
2) if user search for himself, he will fail to go to ShareCoinzActivity and get a hint in snackbar
3) if user search for a unregistered email address, he will fail to go to ShareCoinzActivity and get a hint in snackbar
* */

    //if the emulator asks to update google play service, please make sure to test after updating!
//Sometimes the test will fail because the emulator is working unexpectedly slow and therefore the sleeping time interval is too short..
//Just modify the sleeping interval to pass the test



    //to check whether user can search for an email and then go to ShareCoinzActivity
    @Test
    public void searchRegisteredEmailOfOthersTest() {
        //if the user never signed in before
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
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
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.editText_email_search),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout_message),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("test2@gmail.com"), closeSoftKeyboard()); //this email has been registered

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.imageView_email_search),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout_message),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //this shows the user has been directed to ShareCoinzActivity
        ViewInteraction textView = onView(
                allOf(withText("Coinz you collect yourself in your wallet"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar_share),
                                        childAtPosition(
                                                IsInstanceOf.instanceOf(android.view.ViewGroup.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Coinz you collect yourself in your wallet")));

    }


    //if user search for his own email, he will fail to go to ShareCoinzActivity
    @Test
    public void searchUserOwnEmailTest() {
        //if the user never signed in before
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
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
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.editText_email_search),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout_message),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("test1@gmail.com"), closeSoftKeyboard()); //this email is user's own email

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.imageView_email_search),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout_message),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //a hint will be shown in snackbar
        onView(withId(R.id.snackbar_text))
                .check(matches(withText("you cannot search yourself!")));

    }

    //If user search for a non-existed user, a hint will be shown and he cannot go to ShareCoinzActivity
    @Test
    public void searchNonExistedUserEmailTest() {
        //if the user never signed in before
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
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
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.editText_email_search),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout_message),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("testnotexist@gmail.com"), closeSoftKeyboard()); //this email doesn't exist

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.imageView_email_search),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout_message),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //a hint will be shown in snackbar
        onView(withId(R.id.snackbar_text))
                .check(matches(withText("No such user.")));

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
