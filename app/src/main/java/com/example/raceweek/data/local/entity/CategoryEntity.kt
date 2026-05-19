package com.example.raceweek.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val active: Boolean,
    val description: String = "",
    @ColumnInfo(name = "order") val order: Int = 0
)
