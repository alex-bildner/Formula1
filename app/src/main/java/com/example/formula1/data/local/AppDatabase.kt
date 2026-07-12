package com.example.formula1.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.formula1.data.local.dao.DriverDao
import com.example.formula1.data.local.dao.FavoriteDao
import com.example.formula1.data.local.dao.TeamDao
import com.example.formula1.data.local.entity.DriverEntity
import com.example.formula1.data.local.entity.FavoriteEntity
import com.example.formula1.data.local.entity.TeamEntity

@Database(
    entities = [TeamEntity::class, DriverEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun daoEquipe(): TeamDao
    abstract fun daoPiloto(): DriverDao
    abstract fun daoFavorito(): FavoriteDao
}
