package com.example.raceweek.domain.model

data class Category(
    val id: Int,
    val name: String,
    val active: Boolean,
    val description: String = "",
    val order: Int = 0
)
