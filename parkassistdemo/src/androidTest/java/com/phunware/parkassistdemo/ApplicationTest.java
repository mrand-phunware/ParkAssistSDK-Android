package com.phunware.parkassistdemo;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
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
            MainActivity.class, true, false);

    @Before
    public void launchActivity() {
        Intent launchIntent = new Intent();
        launchIntent.putExtra("Testing", true);
        mActivityRule.launchActivity(launchIntent);
    }

    @Test
    public void testSearch() {
        onView(withId(R.id.plate_input))
                .perform(typeText("ZZZ"), closeSoftKeyboard());
        onView(withId(R.id.submit_search_button)).perform(click());
        onView(withId(R.id.results_recycler))
                .check(matches(withListSize(2)));
    }

    @Test
    public void testZoneFixtures() {
        onView(withId(R.id.zone_recycler))
                .check(matches(withListSize(3)));
        onView(withId(R.id.sign_button))
                .perform(click());
        onView(withId(R.id.zone_recycler))
                .check(matches(withListSize(2)));
    }

    public static Matcher<View> withListSize (final int size) {
        return new TypeSafeMatcher<View>() {
            @Override public boolean matchesSafely (final View view) {
                return ((RecyclerView) view).getChildCount () == size;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText ("ListView should have " + size + " items");
            }
        };
    }


}