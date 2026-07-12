package com.example.formula1.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.formula1.data.repository.TeamRepository
import com.example.formula1.domain.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeamsUiState(
    val isLoading: Boolean = true,
    val carregandoApi: Boolean = false,
    val teams: List<Team> = emptyList(),
    val errorMessage: String? = null
)

class TeamsViewModel(
    private val repository: TeamRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TeamsUiState())
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()

    init {
        observarEquipes()
        atualizar()
    }

    private fun observarEquipes() {
        viewModelScope.launch {
            repository.observarEquipes().collect { teams ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        teams = teams
                    )
                }
            }
        }
    }

    fun atualizar() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = it.teams.isEmpty(),
                    carregandoApi = true,
                    errorMessage = null
                )
            }
            runCatching { repository.atualizarEquipes() }
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

    fun aoAlternarFavorito(team: Team) {
        viewModelScope.launch {
            repository.alternarFavorito(team.teamId, team.isFavorite)
        }
    }

    class Factory(
        private val repository: TeamRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TeamsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TeamsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
