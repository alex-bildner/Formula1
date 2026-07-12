package com.example.formula1.domain.model

data class Driver(
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
