package com.alexandria.chalk

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SavedGymEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChalkDatabase : RoomDatabase() {

    abstract fun savedGymDao(): SavedGymDao

    companion object {

        @Volatile
        private var INSTANCE: ChalkDatabase? = null

        fun getDatabase(context: Context): ChalkDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChalkDatabase::class.java,
                    "chalk_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}