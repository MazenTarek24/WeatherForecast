package com.example.weatherforecast.home.viewmodel

import com.example.weatherforecast.fortesting.FakeRepository
import com.example.weatherforecast.model.onecall.AllWeather
import com.example.weatherforecast.model.onecall.Current
import com.example.weatherforecast.network.ApiState
import com.example.weatherforecast.repo.RepoInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response



@ExperimentalCoroutinesApi
class HomeViewModelTest {

    lateinit var viewModel: HomeViewModel
    lateinit var repository: RepoInterface

    private val fakeFlow = flow {

        var expectedResult = AllWeather(
            1, null,
            Current(98, 10.0, 215562, 12.0, 50, 100, 12,
                5, 50.0, 25.0, 1, listOf(), null, null, 150.01),
            listOf(), listOf(), 1565325.044, 14532.255, null, "23/7", 20
        )


        emit(Response.success(expectedResult))
    }

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun fetchAllWeather_success_Test() = runBlockingTest {
        val fakeRepository = repository as FakeRepository
        fakeRepository.fakeResponse = fakeFlow

        val resultFlow = viewModel.fetchAllWeather(10.0, 20.0, "en")
        val resultStates = resultFlow.toList()

        advanceUntilIdle()
        val expectedStates = listOf(ApiState.Loading, ApiState.Success(fakeFlow.first().body()!!))
        assertEquals(expectedStates, resultStates)

        val cachedData = fakeRepository.fakeCachedData
        assertNotNull(cachedData)
        assertEquals(fakeFlow.first().body()!!, cachedData)

    }
}