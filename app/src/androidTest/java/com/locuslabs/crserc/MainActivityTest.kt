package com.locuslabs.crserc

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun before() {
        IdlingRegistry.getInstance().register(getMyApplication().espressoTestIdlingResource)
    }

    @Test
    fun shouldPerformTwoConcurrentAsyncTasks() {
        onView(withId(R.id.doAsyncTasksButton)).perform(click())

        onView(withId(R.id.result1TextView))
            .check(matches(withText("[showUIAsync]")))

        onView(withId(R.id.result2TextView))
            .check(matches(withText("[startBackendAsync, endBackendAsync]")))
    }

    @After
    fun after() {
        IdlingRegistry.getInstance().unregister(getMyApplication().espressoTestIdlingResource)
    }

    private fun getMyApplication(): MyApplication {
        return InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MyApplication
    }
}
