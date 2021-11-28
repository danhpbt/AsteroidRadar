package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*


//SQLite Date And Time Functions
//https://www.sqlite.org/lang_datefunc.html

//Multiple line query
//https://stackoverflow.com/questions/50892856/kotlin-inject-android-room-sql-language-on-multiple-line-queries

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM databaseasteroid")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("""
            SELECT * FROM databaseasteroid
            WHERE closeApproachDate = date('now') 
            ORDER BY closeApproachDate ASC
            """)
    fun getTodayAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("""
            SELECT * FROM databaseasteroid
            WHERE closeApproachDate BETWEEN date('now') AND date('now', '+7 days')
            ORDER BY closeApproachDate ASC
            """)
    fun getWeekAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    //Using for delete Asteroid the day before
    @Query("DELETE FROM databaseasteroid WHERE closeApproachDate < date('now')")
    fun deletePrevAsteroids(): Int
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids").build()
        }
    }
    return INSTANCE
}
