package com.example.raceweek.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val notifications: String = "T",
    val time: String = "B",
    val practices: String = "T",
    val qualifyings: String = "T",
    val races: String = "T"
)
