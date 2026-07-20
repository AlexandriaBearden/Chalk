package com.alexandria.chalk

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_gyms")
data class SavedGymEntity(

    @PrimaryKey
    val name: String,
    val location: String

    )