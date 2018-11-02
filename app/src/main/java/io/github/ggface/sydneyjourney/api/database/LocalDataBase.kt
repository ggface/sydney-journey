package io.github.ggface.sydneyjourney.api.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import io.github.ggface.sydneyjourney.api.pojo.Venue

/**
 * @author Ivan Novikov on 2018-11-01.
 */
@Database(entities = arrayOf(Venue::class), version = 1, exportSchema = false)
abstract class LocalDataBase : RoomDatabase() {

    abstract fun venueDAO(): VenueDao
}