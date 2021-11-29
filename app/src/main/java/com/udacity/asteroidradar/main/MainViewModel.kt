package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteriodsRepository
import kotlinx.coroutines.launch
import retrofit2.await
import timber.log.Timber

enum class Filter { TODAY, WEEK, ALL }

class MainViewModel(application: Application) :  AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteriodsRepository = AsteriodsRepository(database)

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

    private val _refresh = MutableLiveData<Boolean>()
    val refresh: LiveData<Boolean>
    get() = _refresh

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

    fun requestRefresh()
    {
        viewModelScope.launch {
            try {
                _refresh.value = true;
                asteriodsRepository.refreshAsteriods()
                _pictureOfDay.value = NasaApi.retrofitService.getPictureOfDay().await()

            } catch (e: Exception) {
                Timber.d(e.message)
            }
            finally {
                _refresh.value = false
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