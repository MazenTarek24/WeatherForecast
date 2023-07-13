package com.example.weatherforecast.model

import com.example.weatherforecast.fortesting.FakeLoucalSource
import com.example.weatherforecast.fortesting.FakeRemoteSource
import com.example.weatherforecast.model.onecall.Alert
import com.example.weatherforecast.repo.Repo
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RepoTest {

    val fakeRemoteSource = FakeRemoteSource()
    val fakeLoucalSource = FakeLoucalSource()

    lateinit var repo: Repo

    @Before

    fun createRepo() {
        repo = Repo(fakeRemoteSource, fakeLoucalSource)
    }

    @Test
    fun getAllWeather() = runBlockingTest {

        val response = repo.getAllWeather(10.0, 20.0, "en")

        val actualResponse = response.single()

        val actualWeatherData = actualResponse.body()


        assertEquals(fakeRemoteSource.fakeWeatherData, actualWeatherData)


    }

    @Test
     fun getCashedData() = runBlockingTest {

        val dataInserted = fakeLoucalSource.fakeWeatherData
        repo.insertWeather(dataInserted)
        val repCashed = repo.getCashedData().singleOrNull()
        assertEquals(dataInserted, repCashed)
    }

    @Test
    fun insertAlert() = runBlockingTest {
        val alert = Alert(
            description = "Test Alert",
            startDate = "2023-07-13",
            endDate = "2023-07-14",
            startTime = "08:00",
            endTime = "10:00"
        )

        repo.insertAlert(alert)

        val allAlerts = repo.getAllAlertts().single()
        assertEquals(1, allAlerts.size)
        assertEquals(alert, allAlerts.first())
    }


    @Test
    fun deleteAlert() = runBlockingTest {
        val alert = Alert(
            description = "Test Alert",
            startDate = "2023-07-13",
            endDate = "2023-07-14",
            startTime = "08:00",
            endTime = "10:00"
        )
        repo.insertAlert(alert)

        repo.deleteAlert(alert)

        val allAlerts = repo.getAllAlertts().single()
        assertEquals(0, allAlerts.size)
    }


    @Test
    fun getAllAlerts() = runBlockingTest {
        val alert1 = Alert(
            description = "Alert 1",
            startDate = "2023-07-13",
            endDate = "2023-07-14",
            startTime = "08:00",
            endTime = "10:00"
        )
        val alert2 = Alert(
            description = "Alert 2",
            startDate = "2023-07-15",
            endDate = "2023-07-16",
            startTime = "12:00",
            endTime = "14:00"
        )
        repo.insertAlert(alert1)
        repo.insertAlert(alert2)

        val allAlerts = repo.getAllAlertts().single()

        assertEquals(2, allAlerts.size)
        assertEquals(alert1, allAlerts[0])
        assertEquals(alert2, allAlerts[1])
    }

}