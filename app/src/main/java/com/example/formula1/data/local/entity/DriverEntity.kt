package com.example.formula1.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "drivers",
    primaryKeys = ["teamId", "driverId"]
)
data class DriverEntity(
    val teamId: String,
    val driverId: String,
    val name: String,
    val surname: String,
    val number: Int?,
    val nationality: String,
    val age: Int?,
    val points: Double?,
    val position: Int?,
    val wins: Int?
)
