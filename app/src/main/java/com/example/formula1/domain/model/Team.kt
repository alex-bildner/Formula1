package com.example.formula1.domain.model

data class Team(
    val teamId: String,
    val teamName: String,
    val teamNationality: String,
    val firstAppearance: Int?,
    val season: Int?,
    val points: Double?,
    val position: Int?,
    val wins: Int?,
    val isFavorite: Boolean
)
