package com.example.formula1

import android.app.Application
import androidx.room.Room
import com.example.formula1.data.local.AppDatabase
import com.example.formula1.data.remote.F1ApiService
import com.example.formula1.data.repository.TeamRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Formula1App : Application() {
    lateinit var repository: TeamRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val interceptorLog = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val clienteHttp = OkHttpClient.Builder()
            .addInterceptor(interceptorLog)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://f1api.dev/api/")
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(F1ApiService::class.java)
        val banco = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "formula1.db"
        ).build()

        repository = TeamRepository(
            apiService = api,
            database = banco
        )
    }
}
