package com.example.formula1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.formula1.data.local.entity.DriverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers WHERE teamId = :teamId ORDER BY points DESC, position ASC")
    fun observarPilotosPorEquipe(teamId: String): Flow<List<DriverEntity>>

    @Query("SELECT COUNT(*) FROM drivers WHERE teamId = :teamId")
    suspend fun contarPilotosPorEquipe(teamId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarPilotos(drivers: List<DriverEntity>)

    @Query("DELETE FROM drivers WHERE teamId = :teamId")
    suspend fun deletarPorEquipeId(teamId: String)
}
