package com.example.sykkelapp.ui.profile

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Test
import org.junit.runner.RunWith
import com.example.sykkelapp.R
import com.google.firebase.auth.FirebaseAuth

@MediumTest
@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {

    @Test
    fun testNavigation() {
        // Setup part: Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        val currentUser = FirebaseAuth.getInstance().currentUser

        // Create a graphical scenario for the ProfileFragment
        val scenario = launchFragmentInContainer<ProfileFragment>()

        scenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.mobile_navigation)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // check if user is logged in or not
        if (currentUser == null) {
            // Calling:
            onView(withId(R.id.sign_in_sign_up_button)).perform(click())

            // Assertion part:
            assert(navController.currentDestination?.id == R.id.signUpFragment)
        }
        else {
            // Calling:
            onView(withId(R.id.profile_settings_button)).perform(click())

            // Assertion part:
            assert(navController.currentDestination?.id == R.id.accountSettingsActivity)
        }

    }
}