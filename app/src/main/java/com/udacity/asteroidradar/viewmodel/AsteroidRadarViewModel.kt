package com.udacity.asteroidradar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteriodsRepository
import kotlinx.coroutines.launch

class AsteroidRadarViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteriodsRepository = AsteriodsRepository(database)

    init {
        viewModelScope.launch {
            asteriodsRepository.refreshAsteriods()
        }
    }

    val asteriodList = asteriodsRepository.asteriods

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AsteroidRadarViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AsteroidRadarViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}