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
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import timber.log.Timber

enum class Filter { TODAY, WEEK, ALL }

class MainViewModel(application: Application) :  AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteriodsRepository = AsteriodsRepository(database)

//    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
//    val navigateToSelectedAsteroid: LiveData<Asteroid>
//        get() = _navigateToSelectedAsteroid

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _filter = MutableLiveData(Filter.WEEK)


    val asteroids = Transformations.switchMap(_filter) {
        when (it) {
            Filter.TODAY -> asteriodsRepository.asteriodsToday
            Filter.WEEK -> asteriodsRepository.asteriodsWeek
            else -> asteriodsRepository.asteriods
        }
    }

    init {
        requestAsteroids()
        requestPictureOfDay();

    }

    fun setFilter(filter: Filter) {
        _filter.value = filter
    }

    fun requestAsteroids()
    {
        viewModelScope.launch {
            try {
                asteriodsRepository.refreshAsteriods()
            } catch (e: Exception) {
                Timber.d(e.message)
            }
        }
    }

    fun requestPictureOfDay()
    {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = NasaApi.retrofitService.getPictureOfDay().await()
            } catch (e: Exception) {
                Timber.d(e.message)
            }
        }
    }

//    fun setSelectedAsteroid(asteroid: Asteroid)
//    {
//        _navigateToSelectedAsteroid.value = asteroid
//    }

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