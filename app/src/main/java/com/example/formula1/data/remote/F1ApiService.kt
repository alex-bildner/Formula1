package com.example.formula1.data.remote

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path

interface F1ApiService {
    @GET("current/teams")
    suspend fun obterEquipesAtuais(): JsonObject

    @GET("current/teams/{teamId}/drivers")
    suspend fun obterPilotosDaEquipe(@Path("teamId") teamId: String): JsonObject
}
