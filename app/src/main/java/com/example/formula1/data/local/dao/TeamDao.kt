package com.example.formula1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.formula1.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams ORDER BY teamName ASC")
    fun observarEquipes(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE teamId = :teamId LIMIT 1")
    fun observarEquipe(teamId: String): Flow<TeamEntity?>

    @Query("SELECT * FROM teams WHERE teamId = :teamId LIMIT 1")
    suspend fun buscarEquipePorId(teamId: String): TeamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarEquipes(teams: List<TeamEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarEquipe(team: TeamEntity)
}
