package com.example.sykkelapp.ui.map

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class MapFragmentTest {

    @Test
    fun activeTaskDetails_DisplayedInUi() {
        val fragmentArgs = bundleOf("selectedListItem" to 0)
        launchFragmentInContainer<MapFragment>(fragmentArgs)
    }

}