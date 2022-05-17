package com.example.sykkelapp.data

import com.example.sykkelapp.data.directions.Leg
import com.example.sykkelapp.data.source.FakeDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class RepositoryTest {
    // Test with FakeDataSource
    private lateinit var datasource: FakeDataSource
    private lateinit var repository: RepositoryInterface

    @Before
    fun setUp() {
        datasource = FakeDataSource()
        repository = Repository(datasource)
    }

    @Test
    fun loadBySykkelRoutes()  {
        runBlocking {
            val returnVal = repository.loadBySykkelRoutes()
            assert(returnVal is List<Route>)
        }
    }

}