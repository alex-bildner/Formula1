package com.example.formula1.data.repository

import androidx.room.withTransaction
import com.example.formula1.data.local.AppDatabase
import com.example.formula1.data.local.entity.DriverEntity
import com.example.formula1.data.local.entity.FavoriteEntity
import com.example.formula1.data.local.entity.TeamEntity
import com.example.formula1.data.remote.F1ApiService
import com.example.formula1.data.remote.dto.paraCargaPilotosEquipe
import com.example.formula1.data.remote.dto.paraListaDtoEquipes
import com.example.formula1.domain.model.Driver
import com.example.formula1.domain.model.Team
import com.example.formula1.domain.model.TeamDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TeamRepository(
    private val apiService: F1ApiService,
    private val database: AppDatabase
) {
    private val teamDao = database.daoEquipe()
    private val driverDao = database.daoPiloto()
    private val favoriteDao = database.daoFavorito()

    fun observarEquipes(): Flow<List<Team>> {
        return combine(teamDao.observarEquipes(), favoriteDao.observarFavoritos()) { teams, favorites ->
            val favoriteIds = favorites.map { it.teamId }.toSet()
            teams.map { it.paraDominio(isFavorite = it.teamId in favoriteIds) }
        }
    }

    fun observarDetalhesEquipe(teamId: String): Flow<TeamDetail?> {
        return combine(
            teamDao.observarEquipe(teamId),
            driverDao.observarPilotosPorEquipe(teamId),
            favoriteDao.observarFavoritos()
        ) { team, drivers, favorites ->
            team ?: return@combine null
            val favoriteIds = favorites.map { it.teamId }.toSet()
            TeamDetail(
                team = team.paraDominio(isFavorite = team.teamId in favoriteIds),
                drivers = drivers.map { it.paraDominio() }
            )
        }
    }

    suspend fun atualizarEquipes() {
        val response = apiService.obterEquipesAtuais()
        val teams = response.paraListaDtoEquipes()
        if (teams.isEmpty()) return

        teamDao.salvarEquipes(
            teams.map {
                TeamEntity(
                    teamId = it.teamId,
                    teamName = it.teamName,
                    teamNationality = it.teamNationality,
                    firstAppearance = it.firstAppeareance,
                    season = it.season,
                    points = it.points,
                    position = it.position,
                    wins = it.wins
                )
            }
        )
    }

    suspend fun atualizarPilotos(teamId: String) {
        val payload = apiService.obterPilotosDaEquipe(teamId).paraCargaPilotosEquipe(teamId)
        val pilotosMapeados = payload.drivers.map { driver ->
            DriverEntity(
                teamId = teamId,
                driverId = driver.driverId,
                name = driver.name,
                surname = driver.surname,
                number = driver.number,
                nationality = driver.nationality,
                age = driver.age,
                points = driver.points,
                position = driver.position,
                wins = driver.wins
            )
        }

        database.withTransaction {
            payload.team?.let { remoteTeam ->
                val localTeam = teamDao.buscarEquipePorId(teamId)
                teamDao.salvarEquipe(
                    TeamEntity(
                        teamId = remoteTeam.teamId,
                        teamName = remoteTeam.teamName.ifBlank { localTeam?.teamName ?: teamId },
                        teamNationality = remoteTeam.teamNationality.ifBlank { localTeam?.teamNationality ?: "Unknown" },
                        firstAppearance = remoteTeam.firstAppeareance ?: localTeam?.firstAppearance,
                        season = remoteTeam.season ?: localTeam?.season,
                        points = remoteTeam.points ?: localTeam?.points,
                        position = remoteTeam.position ?: localTeam?.position,
                        wins = remoteTeam.wins ?: localTeam?.wins
                    )
                )
            }

            val quantidadeLocal = driverDao.contarPilotosPorEquipe(teamId)
            val respostaVaziaInesperada = pilotosMapeados.isEmpty() && quantidadeLocal > 0
            if (!respostaVaziaInesperada) {
                driverDao.deletarPorEquipeId(teamId)
                if (pilotosMapeados.isNotEmpty()) {
                    driverDao.salvarPilotos(pilotosMapeados)
                }
            }
        }
    }

    suspend fun alternarFavorito(teamId: String, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.deletarFavorito(teamId)
        } else {
            favoriteDao.inserirFavorito(FavoriteEntity(teamId))
        }
    }
}

private fun TeamEntity.paraDominio(isFavorite: Boolean): Team {
    return Team(
        teamId = teamId,
        teamName = teamName,
        teamNationality = teamNationality,
        firstAppearance = firstAppearance,
        season = season,
        points = points,
        position = position,
        wins = wins,
        isFavorite = isFavorite
    )
}

private fun DriverEntity.paraDominio(): Driver {
    return Driver(
        driverId = driverId,
        name = name,
        surname = surname,
        number = number,
        nationality = nationality,
        age = age,
        points = points,
        position = position,
        wins = wins
    )
}
