package com.example.sykkelapp.ui.map

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.sykkelapp.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MapFragmentTest {

    @Before
    fun setup() {
        launchFragmentInContainer<MapFragment>()
    }

    @Test
    fun elements_is_displayed() {
        // animations has to be turned off on emulator
        onView(withId(R.id.mapView)).check(matches(isDisplayed()))
        onView(withId(R.id.air_quality_button)).check(matches(isDisplayed()))
        onView(withId(R.id.weather_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.wind_speed)).check(matches(isDisplayed()))
        onView(withId(R.id.wind_direction)).check(matches(isDisplayed()))
        onView(withId(R.id.wind_unit)).check(matches(isDisplayed()))
        onView(withId(R.id.temperature)).check(matches(isDisplayed()))
        onView(withId(R.id.uv_icon)).check(matches(isDisplayed()))
    }

    @Test
    fun on_AirQualityButtonPressed() {
        onView(withId(R.id.air_quality_button)).perform(click()).check(matches(isDisplayed()))
    }

}