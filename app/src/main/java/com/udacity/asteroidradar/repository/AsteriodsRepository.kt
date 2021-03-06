package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.scalar2NetworkAsteroids
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*

class AsteriodsRepository(private val database: AsteroidsDatabase) {

    val asteriods: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    var asteriodsToday: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getTodayAsteroids()) {
            it.asDomainModel()
        }

    var asteriodsWeek: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getWeekAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteriods() {
        withContext(Dispatchers.IO) {
            var calendar = Calendar.getInstance();
            val startDate = calendar.time;

            calendar.add(Calendar.DATE, Constants.DEFAULT_END_DATE_DAYS)
            var endDate = calendar.time

            var sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
            var startDateStr = sdf.format(startDate)
            var endDateStr = sdf.format(endDate)

            val rawData = NasaApi.retrofitService.getAsteroids(startDateStr, endDateStr).await()
            var asteroidList = scalar2NetworkAsteroids(rawData)
            database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())

        }
    }

    suspend fun deletePrevAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deletePrevAsteroids()
        }
    }
}
