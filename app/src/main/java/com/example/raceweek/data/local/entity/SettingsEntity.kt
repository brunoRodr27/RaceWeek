package com.example.raceweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val notifications: Boolean = true,
    val time: String = "B",
    val practices: Boolean = true,
    val qualifyings: Boolean = true,
    val races: Boolean = true
)
