package com.example.formula1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.formula1.Formula1App
import com.example.formula1.ui.teamdetail.RotaDetalheEquipe
import com.example.formula1.ui.teamdetail.TeamDetailViewModel
import com.example.formula1.ui.teams.RotaEquipes
import com.example.formula1.ui.teams.TeamsViewModel

private const val ROUTE_TEAMS = "teams"
private const val ROUTE_TEAM_DETAIL = "team-detail/{teamId}"

@Composable
fun GraficoNavegacaoApp(
    app: Formula1App,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ROUTE_TEAMS,
        modifier = modifier
    ) {
        composable(ROUTE_TEAMS) {
            val teamsViewModel: TeamsViewModel = viewModel(
                factory = TeamsViewModel.Factory(app.repository)
            )
            RotaEquipes(
                viewModel = teamsViewModel,
                onTeamClick = { teamId ->
                    navController.navigate("team-detail/$teamId")
                }
            )
        }

        composable(
            route = ROUTE_TEAM_DETAIL,
            arguments = listOf(
                navArgument("teamId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId").orEmpty()
            val teamDetailViewModel: TeamDetailViewModel = viewModel(
                factory = TeamDetailViewModel.Factory(teamId, app.repository)
            )
            RotaDetalheEquipe(
                viewModel = teamDetailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
