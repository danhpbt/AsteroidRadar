package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteriodsRepository
import com.udacity.asteroidradar.viewmodel.AsteroidRadarViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

enum class Filter { TODAY, WEEK, SAVED }

class MainViewModel(application: Application) :  AndroidViewModel(application) {

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _filter = MutableLiveData(Filter.WEEK)

    val asteroids = Transformations.switchMap(_filter) {
/*        when (it) {
            *//*Filter.TODAY -> asteroidRepository.todaysAsteroids
            Filter.WEEK -> asteroidRepository.weeksAsteroids
            else -> asteroidRepository.asteroids*//*

            asteriodsRepository.asteriods
        }*/
        asteriodsRepository.asteriods
    }

    private val database = getDatabase(application)
    private val asteriodsRepository = AsteriodsRepository(database)


    init {
        getAsteroids()
        getPictureOfDay();

    }

    fun getAsteroids()
    {
        viewModelScope.launch {
            try {
                asteriodsRepository.refreshAsteriods()
            } catch (e: Exception) {
                Timber.d(e.message)
            }
        }
    }

    fun getPictureOfDay()
    {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = NasaApi.retrofitService.getPictureOfDay()
            } catch (e: Exception) {
                Timber.d(e.message)
            }
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}