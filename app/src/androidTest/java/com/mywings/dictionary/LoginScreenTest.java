package com.mywings.dictionary;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginScreenTest {

    @Rule
    public ActivityTestRule<LoginScreen> mActivityTestRule = new ActivityTestRule<>(LoginScreen.class);

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

    @Test
    public void loginScreenTest() {

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction txtUserName = onView(
                allOf(withId(R.id.txtUserName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tilUserName),
                                        0),
                                0),
                        isDisplayed()));

        txtUserName.check(matches(isDisplayed()));

   /*        ViewInteraction txtPassword = onView(
                allOf(withId(R.id.txtPassword),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tilPassword),
                                        0),
                                0),
                        isDisplayed()));
        txtPassword.check(matches(isDisplayed()));

       ViewInteraction btnSignIn = onView(
                allOf(withId(R.id.btnSignIn),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                0),
                        isDisplayed()));
        btnSignIn.check(matches(isDisplayed()));

      ViewInteraction btnGoogleSignIn = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.btnGoogleSignIn),
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                        3)),
                        0),
                        isDisplayed()));
        btnGoogleSignIn.check(matches(isDisplayed()));

        ViewInteraction btnFacebookLogIn = onView(
                allOf(withId(R.id.btnFacebookSignIn),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                4),
                        isDisplayed()));
        btnFacebookLogIn.check(matches(isDisplayed()));

        ViewInteraction btnSignUp = onView(
                allOf(withId(R.id.btnSignUp),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                5),
                        isDisplayed()));
        btnSignUp.check(matches(isDisplayed()));*/


    }
}
