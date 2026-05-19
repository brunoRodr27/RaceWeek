package com.example.raceweek.domain.util

fun String.toSessionLabel(): String = when (this.lowercase()) {
    "practice"        -> "Treino Livre"
    "practiceone"     -> "Treino Livre 1"
    "practicetwo"     -> "Treino Livre 2"
    "practicethree"   -> "Treino Livre 3"
    "qualifying"      -> "Classificação"
    "sprintqualifying"-> "Classificação - Sprint"
    "sprint"          -> "Corrida - Sprint"
    "race"            -> "Corrida"
    else              -> this.replaceFirstChar { it.uppercase() }
}
