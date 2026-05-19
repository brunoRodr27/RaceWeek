package com.example.raceweek.presentation.utils

import com.example.raceweek.domain.util.toSessionLabel

fun String.toSessionDisplayName(): String = this.toSessionLabel()
