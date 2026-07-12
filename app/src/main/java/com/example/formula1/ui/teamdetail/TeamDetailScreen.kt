package com.example.formula1.ui.teamdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.formula1.domain.model.Driver
import com.example.formula1.domain.model.TeamDetail
import com.example.formula1.ui.theme.HeaderBackground
import com.example.formula1.ui.theme.MetadataText
import com.example.formula1.ui.theme.TeamCardBorder

@Composable
fun RotaDetalheEquipe(
    viewModel: TeamDetailViewModel = viewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    TelaDetalheEquipe(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onToggleFavorite = viewModel::aoAlternarFavorito
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TelaDetalheEquipe(
    state: TeamDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                title = { Text("Pilotos da Escuderia") },
                actions = {
                    val isFavorite = state.detail?.team?.isFavorite ?: false
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritar equipe"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.detail == null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Dados da equipe indisponiveis offline.")
                    }
                }

                else -> {
                    ConteudoDetalheEquipe(
                        detail = state.detail,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (state.carregandoApi && !state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ConteudoDetalheEquipe(
    detail: TeamDetail,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderBackground)
                    .padding(10.dp)
            ) {
                Text(
                    text = detail.team.teamName.uppercase(),
                    fontWeight = FontWeight.Bold
                )
                Text("Perfil da equipe (${detail.team.season ?: "-" } temporada)")
                Text("Pontos: ${detail.team.points ?: "-"}")
                Text("Posicao: ${detail.team.position ?: "-"}")
                Text("Vitorias: ${detail.team.wins ?: "-"}")
            }
        }

        items(
            items = detail.drivers,
            key = { it.driverId }
        ) { driver ->
            CardPiloto(driver = driver)
        }
    }
}

@Composable
private fun CardPiloto(driver: Driver) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, TeamCardBorder),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${driver.name} ${driver.surname}",
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text("nacionalidade: ${driver.nationality}", color = MetadataText)
                Text("idade: ${driver.age ?: "-"} anos", color = MetadataText)
                Text(
                    text = "pontos: ${driver.points ?: "-"}, posicao: ${driver.position ?: "-"}, vitorias: ${driver.wins ?: "-"}",
                    color = MetadataText
                )
            }
            Text(
                text = driver.number?.toString() ?: "-",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
