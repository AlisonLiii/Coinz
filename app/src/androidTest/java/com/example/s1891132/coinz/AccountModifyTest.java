package com.example.s1891132.coinz;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
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

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

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

 /*To test :
    1)whether user can modify their username and bio in their account page.
    2)whether user's information like id, email address, camp and walking distance has been displayed
  */


@RunWith(AndroidJUnit4.class)
public class AccountModifyTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);
    @Rule
    public GrantPermissionRule grantPermissionRule=GrantPermissionRule .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void accountModifyTest() {


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
            Thread.sleep(3000);
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
                        1),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        // replace the username with "change"
        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.edit_name),
                        childAtPosition(
                                allOf(withId(R.id.fragment_account),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                2),
                        isDisplayed()));
        textInputEditText.perform(replaceText("change"),closeSoftKeyboard());


        // replace the user's bio with "change"
        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.edit_bio),
                        childAtPosition(
                                allOf(withId(R.id.fragment_account),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                3),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("change"),closeSoftKeyboard());


        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.save_account_info), withText("Save changes"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_account),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                8),
                        isDisplayed()));
        appCompatButton4.perform(click());

        //to see whether changes have saved
        ViewInteraction textView = onView(
                allOf(withId(R.id.snackbar_text), withText("Changes saved!"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.instanceOf(android.widget.FrameLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Changes saved!")));


        FragmentManager fragmentManager= new FragmentManager() {
            @Override
            public FragmentTransaction beginTransaction() {
                return null;
            }

            @Override
            public boolean executePendingTransactions() {
                return false;
            }

            @Override
            public Fragment findFragmentById(int i) {
                return null;
            }

            @Override
            public Fragment findFragmentByTag(String s) {
                return null;
            }

            @Override
            public void popBackStack() {

            }

            @Override
            public boolean popBackStackImmediate() {
                return false;
            }

            @Override
            public void popBackStack(String s, int i) {

            }

            @Override
            public boolean popBackStackImmediate(String s, int i) {
                return false;
            }

            @Override
            public void popBackStack(int i, int i1) {

            }

            @Override
            public boolean popBackStackImmediate(int i, int i1) {
                return false;
            }

            @Override
            public int getBackStackEntryCount() {
                return 0;
            }

            @Override
            public BackStackEntry getBackStackEntryAt(int i) {
                return null;
            }

            @Override
            public void addOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {

            }

            @Override
            public void removeOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {

            }

            @Override
            public void putFragment(Bundle bundle, String s, Fragment fragment) {

            }

            @Override
            public Fragment getFragment(Bundle bundle, String s) {
                return null;
            }

            @Override
            public List<Fragment> getFragments() {
                return null;
            }

            @Override
            public Fragment.SavedState saveFragmentInstanceState(Fragment fragment) {
                return null;
            }

            @Override
            public boolean isDestroyed() {
                return false;
            }

            @Override
            public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks fragmentLifecycleCallbacks, boolean b) {

            }

            @Override
            public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks fragmentLifecycleCallbacks) {

            }

            @Override
            public Fragment getPrimaryNavigationFragment() {
                return null;
            }

            @Override
            public void dump(String s, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strings) {

            }

            @Override
            public boolean isStateSaved() {
                return false;
            }
        };

        //go back to the navigation menu
        fragmentManager.popBackStack();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction navigationMenuItemView2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navigation_view),
                                        0)),
                        1),
                        isDisplayed()));
        navigationMenuItemView2.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //enter my account page again to see whether the name has been changed
        ViewInteraction editText = onView(
                allOf(withId(R.id.edit_name), withText("change"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_account),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                2),
                        isDisplayed()));
        editText.check(matches(withText("change")));


        //enter my account page again to see whether the bio has been changed
        ViewInteraction editText2 = onView(
                allOf(withId(R.id.edit_bio), withText("change"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_account),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                3),
                        isDisplayed()));
        editText2.check(matches(withText("change")));


        //to check whether user's id has been displayed
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.uid), withText("47HZ910P0ldc9c9YFVhIXXqIU2x2"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_account),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                4),
                        isDisplayed()));
        textView2.check(matches(withText("47HZ910P0ldc9c9YFVhIXXqIU2x2")));


        //to check whether user's email address has been displayed
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.email_address_text), withText("test1@gmail.com"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_account),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                5),
                        isDisplayed()));
        textView3.check(matches(withText("test1@gmail.com")));


        //to check whether user's camp has been displayed
        ViewInteraction textView4 = onView(
                allOf(withId(R.id.camp_text), withText("AI"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_account),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                6),
                        isDisplayed()));
        textView4.check(matches(withText("AI")));


        //to check whether user's walking distance of the day has been displayed
        ViewInteraction textView5 = onView(
                allOf(withId(R.id.walking_distance_text), withText("0.0"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_account),
                                        childAtPosition(
                                                withId(R.id.fragment_frame),
                                                1)),
                                7),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

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
