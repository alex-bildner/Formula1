package com.example.formula1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.formula1.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun observarFavoritos(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirFavorito(entidade: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE teamId = :teamId")
    suspend fun deletarFavorito(teamId: String)
}
