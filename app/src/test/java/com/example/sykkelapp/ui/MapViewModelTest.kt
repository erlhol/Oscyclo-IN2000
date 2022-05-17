package com.example.sykkelapp.ui

import androidx.test.core.app.ApplicationProvider
import com.example.sykkelapp.data.RepositoryInterface
import com.example.sykkelapp.data.source.FakeDataSource
import com.example.sykkelapp.data.source.FakeRepository
import com.example.sykkelapp.ui.map.MapViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MapViewModelTest {

    private lateinit var repository: RepositoryInterface
    private lateinit var viewModel : MapViewModel

    @Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        repository = FakeRepository(FakeDataSource())
        viewModel = MapViewModel(ApplicationProvider.getApplicationContext(),repository)
    }

    @Test
    fun testUpdateLocation() {
        // Given:
        val pre = viewModel.weatherforecast.value

        // On call:
        viewModel.updateLocation()

        // Assert:
        assert(viewModel.weatherforecast.value == pre)
    }
}