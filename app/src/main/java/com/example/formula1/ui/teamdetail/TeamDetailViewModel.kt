package com.example.formula1.ui.teamdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.formula1.data.repository.TeamRepository
import com.example.formula1.domain.model.TeamDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeamDetailUiState(
    val isLoading: Boolean = true,
    val carregandoApi: Boolean = false,
    val detail: TeamDetail? = null,
    val errorMessage: String? = null
)

class TeamDetailViewModel(
    private val teamId: String,
    private val repository: TeamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamDetailUiState())
    val uiState: StateFlow<TeamDetailUiState> = _uiState.asStateFlow()

    init {
        observarDados()
        atualizar()
    }

    private fun observarDados() {
        viewModelScope.launch {
            repository.observarDetalhesEquipe(teamId).collect { detail ->
                _uiState.update { estado ->
                    estado.copy(
                        detail = detail,
                        // Mantem loading enquanto chamada da API estiver ativa e sem dados locais.
                        isLoading = estado.carregandoApi && detail == null
                    )
                }
            }
        }
    }

    fun atualizar() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = it.detail == null,
                    carregandoApi = true,
                    errorMessage = null
                )
            }
            runCatching { repository.atualizarPilotos(teamId) }
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            carregandoApi = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            carregandoApi = false,
                            errorMessage = "Sem internet. Exibindo dados salvos."
                        )
                    }
                }
        }
    }

    fun aoAlternarFavorito() {
        val currentTeam = _uiState.value.detail?.team ?: return
        viewModelScope.launch {
            repository.alternarFavorito(currentTeam.teamId, currentTeam.isFavorite)
        }
    }

    class Factory(
        private val teamId: String,
        private val repository: TeamRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TeamDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TeamDetailViewModel(teamId, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
