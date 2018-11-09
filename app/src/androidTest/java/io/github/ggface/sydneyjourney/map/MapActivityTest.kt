package io.github.ggface.sydneyjourney.map

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.github.ggface.sydneyjourney.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testing [MapActivity]
 *
 * @author Ivan Novikov on 2018-11-09.
 */
@RunWith(AndroidJUnit4::class)
class MapActivityTest {

    @Rule
    @JvmField
    var activityTestRule: ActivityTestRule<MapActivity> = ActivityTestRule(MapActivity::class.java)

    @Test
    fun checkContainerIsDisplayed() {
        onView(ViewMatchers.withId(R.id.map_view)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.list_button)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.venues_button)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.location_button)).check(matches(isDisplayed()))
    }
}