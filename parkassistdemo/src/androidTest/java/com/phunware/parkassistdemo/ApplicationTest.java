package com.phunware.parkassistdemo;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;


@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void testSearchTextDoesntDelete() {
        onView(withId(R.id.plate_input))
                .perform(typeText("ZZZ"), closeSoftKeyboard());
        onView(withId(R.id.submit_search_button)).perform(click());
        onView(withId(R.id.plate_input))
                .check(matches(withText("ZZZ")));
    }

}