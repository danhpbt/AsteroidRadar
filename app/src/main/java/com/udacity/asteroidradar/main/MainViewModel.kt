package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.NasaApiStatus
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _status = MutableLiveData<NasaApiStatus>()
    val status: LiveData<NasaApiStatus>
        get() = _status

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        var calendar = Calendar.getInstance();
        val startDate = calendar.time;

        calendar.add(Calendar.DATE, 7)
        var endDate = calendar.time

        val sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
        val startDateStr = sdf.format(startDate)
        val endDateStr = sdf.format(endDate)

        getAsteroids(startDateStr, endDateStr, BuildConfig.NASA_API_KEY)
    }

    //private fun getAsteroids(startDate: Date, numEndDays : Int) {
    private fun getAsteroids(startDate: String, endDate: String, apiKey: String) {
        _status.value = NasaApiStatus.LOADING
        NasaApi.retrofitService.getAsteroids(startDate, endDate, apiKey).enqueue( object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                val errVal = t.message
                _status.value = NasaApiStatus.ERROR
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val resVal = response.body()
                val jsonObj = JSONObject(resVal)
                _asteroids.value = parseAsteroidsJsonResult(jsonObj)
                _status.value = NasaApiStatus.DONE
            }
        })

/*        viewModelScope.launch {
            _status.value = NasaApiStatus.LOADING
            try {
                //_asteroids.value = MarsApi.retrofitService.getProperties(filter.value)
                _status.value = NasaApiStatus.DONE
            } catch (e: Exception) {
                _status.value = NasaApiStatus.ERROR
                _asteroids.value = ArrayList()
            }
        }*/
    }
}