package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.scalar2NetworkAsteroids
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AsteriodsRepository(private val database: AsteroidsDatabase) {

    val asteriods: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteriods() {
        withContext(Dispatchers.IO) {
            var calendar = Calendar.getInstance();
            val startDate = calendar.time;

            calendar.add(Calendar.DATE, 7)
            var endDate = calendar.time

            var sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
            var startDateStr = sdf.format(startDate)
            var endDateStr = sdf.format(endDate)

            val rawData = NasaApi.retrofitService.getAsteroids(startDateStr, endDateStr)
            var asteroidList = scalar2NetworkAsteroids(rawData)
            database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())
        }
    }
}
