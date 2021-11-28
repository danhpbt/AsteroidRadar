package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

//check log in debug under okhttp
private val httpLoggingInterceptor: HttpLoggingInterceptor by lazy{
    val httpLoggingInterceptor1 = HttpLoggingInterceptor()
    httpLoggingInterceptor1.apply {
        if (BuildConfig.DEBUG)
            httpLoggingInterceptor1.level = HttpLoggingInterceptor.Level.BODY
    }
}

//http client
private val httpClient: OkHttpClient by lazy{
        OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30,TimeUnit.SECONDS)
        .writeTimeout(30,TimeUnit.SECONDS)
        .addInterceptor(httpLoggingInterceptor)
        .build()
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        AnnotatedConverterFactory.Builder()
            .add(AnnotatedConverterFactory.Json::class, MoshiConverterFactory.create(moshi))
            .add(AnnotatedConverterFactory.Scalar::class, ScalarsConverterFactory.create())
            .build())
    .baseUrl(Constants.BASE_URL)
    .client(httpClient)
    .build()

interface NasaApiService {
    //https://api.nasa.gov/neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=DEMO_KEY
    @GET("neo/rest/v1/feed")
    @AnnotatedConverterFactory.Scalar
    fun getAsteroids(@Query("start_date") startDate: String,
                              @Query("end_date") endDate: String,
                              @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY): Call<String>

    //https://api.nasa.gov/planetary/apod?api_key=YOUR_API_KEY
    @GET("planetary/apod")
    @AnnotatedConverterFactory.Json
    fun getPictureOfDay(@Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY) : Call<PictureOfDay>

}

object NasaApi {
    val retrofitService : NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}