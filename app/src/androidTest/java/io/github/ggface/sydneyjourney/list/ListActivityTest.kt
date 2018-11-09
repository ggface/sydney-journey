package io.github.ggface.sydneyjourney.list

import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.github.ggface.sydneyjourney.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testing [ListActivity]
 *
 * @author Ivan Novikov on 2018-11-09.
 */
@RunWith(AndroidJUnit4::class)
class ListActivityTest {

    @Rule
    @JvmField
    var activityTestRule: ActivityTestRule<ListActivity> = ActivityTestRule(ListActivity::class.java)

    @Test
    fun checkContainerIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.toolbar)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.venues_recycler_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}