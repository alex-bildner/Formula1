package com.example.formula1.data.remote.dto

data class TeamDto(
    val teamId: String,
    val teamName: String,
    val teamNationality: String,
    val firstAppeareance: Int?,
    val season: Int?,
    val points: Double?,
    val position: Int?,
    val wins: Int?
)
