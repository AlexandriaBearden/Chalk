package com.alexandria.chalk

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedGymDao {

    @Query("SELECT * FROM saved_gyms")
    fun getAllSavedGyms(): Flow<List<SavedGymEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGym(gym: SavedGymEntity)

    @Delete
    suspend fun deleteGym(gym: SavedGymEntity)
}