package com.howto.coredux

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import it.xabaras.android.espresso.recyclerviewchildactions.RecyclerViewChildActions
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

    @After
    fun after() {
        IdlingRegistry.getInstance().unregister(getMyApplication().espressoTestIdlingResource)
    }

    private fun getMyApplication(): HowToApplication {
        return InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as HowToApplication
    }

    @Test
    fun showListOfHowToVideos() {
        val videoListSimplified = listOf(
            HowToVideoSimplified("How To Properly Wash the Dishes"),
            HowToVideoSimplified("How To Make Bubble Tea"),
            HowToVideoSimplified("How To Make Salsa")
        )

        videoListSimplified.forEachIndexed { index, howToVideoListSimplified ->
            onView(withId(R.id.videoListRecyclerView)).perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                    index
                )
            )

            onView(withId(R.id.videoListRecyclerView)).check(
                matches(
                    RecyclerViewChildActions.childOfViewAtPositionWithMatcher(
                        R.id.videoNameTextView,
                        index,
                        withText(howToVideoListSimplified.name)
                    )
                )
            )
        }
    }
}
