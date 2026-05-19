package com.example.raceweek.domain.util

fun String.toSessionLabel(): String = when (this.lowercase()) {
    "practice"        -> "Treino Livre"
    "practiceone"     -> "Treino Livre 1"
    "practicetwo"     -> "Treino Livre 2"
    "practicethree"   -> "Treino Livre 3"
    "qualifying"      -> "Classificação"
    "qualifyingone"   -> "Classificação 1"
    "qualifyingtwo"   -> "Classificação 2"
    "qualifyingthree" -> "Classificação 3"
    "sprintqualifying"-> "Classificação - Sprint"
    "sprint"          -> "Corrida - Sprint"
    "race"            -> "Corrida"

    // Treinos Indianapolis 500
    "practiceone12"   -> "Treino Livre 1 (1/2)"
    "practiceone22"   -> "Treino Livre 1 (2/2)"
    "practiceotwo12"  -> "Treino Livre 2 (1/2)"
    "practiceotwo22"  -> "Treino Livre 2 (2/2)"
    "practiceothree12"-> "Treino Livre 3 (1/2)"
    "practiceothree22"-> "Treino Livre 3 (2/2)"
    "practicesix"     -> "Treino Livre 6"
    "practiceseven"   -> "Treino Livre 7"
    else              -> this.replaceFirstChar { it.uppercase() }
}
