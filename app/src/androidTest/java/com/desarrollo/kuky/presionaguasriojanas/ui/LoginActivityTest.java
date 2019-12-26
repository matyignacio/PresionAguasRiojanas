package com.desarrollo.kuky.presionaguasriojanas.ui;

import com.desarrollo.kuky.presionaguasriojanas.R;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivity
            = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void testSetup() throws IOException {
        onView(withId(R.id.etMail)).perform(replaceText(""));
        onView(withId(R.id.etClave)).perform(replaceText("12345"));
        onView(withId(R.id.bLogin)).perform(click());
    }

}