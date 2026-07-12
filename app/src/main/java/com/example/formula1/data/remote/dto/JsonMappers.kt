package com.example.formula1.data.remote.dto

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.Calendar

private fun JsonObject.textoDe(vararg chaves: String): String? {
    for (chave in chaves) {
        val valor = get(chave) ?: continue
        if (!valor.isJsonNull) return valor.asString
    }
    return null
}

private fun JsonObject.inteiroDe(vararg chaves: String): Int? {
    for (chave in chaves) {
        val valor = get(chave) ?: continue
        if (!valor.isJsonNull) return valor.asInt
    }
    return null
}

private fun JsonObject.decimalDe(vararg chaves: String): Double? {
    for (chave in chaves) {
        val valor = get(chave) ?: continue
        if (!valor.isJsonNull) return valor.asDouble
    }
    return null
}

private fun JsonElement.comoObjetoOuNulo(): JsonObject? = if (isJsonObject) asJsonObject else null

private fun extrairAno(data: String): Int? {
    val match = Regex("""(\d{4})""").find(data) ?: return null
    return match.value.toIntOrNull()
}

private fun idadeAPartirDoNascimento(dataNascimento: String?): Int? {
    val anoNascimento = dataNascimento?.let { extrairAno(it) } ?: return null
    val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
    return (anoAtual - anoNascimento).takeIf { it in 15..80 }
}

fun JsonObject.paraListaDtoEquipes(): List<TeamDto> {
    val temporada = inteiroDe("season")
    val arrayEquipes = getAsJsonArray("teams") ?: return emptyList()
    return arrayEquipes.mapNotNull { elemento ->
        val obj = elemento.comoObjetoOuNulo() ?: return@mapNotNull null
        val teamId = obj.textoDe("teamId") ?: return@mapNotNull null
        TeamDto(
            teamId = teamId,
            teamName = obj.textoDe("teamName") ?: teamId.replace('_', ' ').replaceFirstChar { it.uppercase() },
            teamNationality = obj.textoDe("teamNationality") ?: "Unknown",
            firstAppeareance = obj.inteiroDe("firstAppeareance", "firstAppearance"),
            season = obj.inteiroDe("season") ?: temporada,
            points = obj.decimalDe("points"),
            position = obj.inteiroDe("position"),
            wins = obj.inteiroDe("wins")
        )
    }
}

data class TeamDriversPayload(
    val team: TeamDto?,
    val drivers: List<DriverDto>
)

fun JsonObject.paraCargaPilotosEquipe(teamId: String): TeamDriversPayload {
    val objEquipe = get("team")?.comoObjetoOuNulo() ?: this
    val equipe = TeamDto(
        teamId = objEquipe.textoDe("teamId") ?: textoDe("teamId") ?: teamId,
        teamName = objEquipe.textoDe("teamName") ?: "",
        teamNationality = objEquipe.textoDe("teamNationality") ?: "",
        firstAppeareance = objEquipe.inteiroDe("firstAppeareance", "firstAppearance"),
        season = inteiroDe("season"),
        points = objEquipe.decimalDe("points"),
        position = objEquipe.inteiroDe("position"),
        wins = objEquipe.inteiroDe("wins")
    )

    val arrayPilotos = getAsJsonArray("drivers") ?: return TeamDriversPayload(equipe, emptyList())
    val pilotos = arrayPilotos.mapNotNull { elemento ->
        val obj = elemento.comoObjetoOuNulo() ?: return@mapNotNull null
        val objPiloto = obj.get("driver")?.comoObjetoOuNulo() ?: obj
        val driverId = objPiloto.textoDe("driverId")
            ?: listOfNotNull(objPiloto.textoDe("name"), objPiloto.textoDe("surname"))
                .joinToString("_")
                .lowercase()
        if (driverId.isBlank()) return@mapNotNull null

        DriverDto(
            driverId = driverId,
            name = objPiloto.textoDe("name") ?: "",
            surname = objPiloto.textoDe("surname") ?: "",
            number = objPiloto.inteiroDe("number"),
            nationality = objPiloto.textoDe("nationality") ?: "Unknown",
            age = objPiloto.inteiroDe("age") ?: idadeAPartirDoNascimento(objPiloto.textoDe("birthday")),
            points = objPiloto.decimalDe("points"),
            position = objPiloto.inteiroDe("position"),
            wins = objPiloto.inteiroDe("wins")
        )
    }
    return TeamDriversPayload(equipe, pilotos)
}
