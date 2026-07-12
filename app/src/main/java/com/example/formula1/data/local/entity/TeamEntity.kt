package com.example.formula1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val teamId: String,
    val teamName: String,
    val teamNationality: String,
    val firstAppearance: Int?,
    val season: Int?,
    val points: Double?,
    val position: Int?,
    val wins: Int?
)
