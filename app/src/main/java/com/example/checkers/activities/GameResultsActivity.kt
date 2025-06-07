package com.example.checkers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.checkers.R
import com.example.checkers.activities.ui.theme.CheckersTheme
import com.example.checkers.components.GoBackAppBar
import com.example.checkers.data.local.entity.GameResult
import com.example.checkers.viewmodels.ResultViewModel

class GameResultsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val resultViewModel: ResultViewModel by viewModels()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            val widthClass = windowSizeClass.widthSizeClass
            val heightClass = windowSizeClass.heightSizeClass


            val isTablet = widthClass == WindowWidthSizeClass.Medium && heightClass == WindowHeightSizeClass.Medium
                                || widthClass == WindowWidthSizeClass.Expanded || heightClass == WindowHeightSizeClass.Expanded


            if (isTablet) {
                CheckersTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = { GoBackAppBar(stringResource(R.string.game_register)) },
                        containerColor = Color(0xFF2E3B4E)
                    ) { innerPadding ->
                        TabletDisplay(resultViewModel, modifier = Modifier.padding(innerPadding))

                    }
                }
            } else {
                CheckersTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = { GoBackAppBar(stringResource(R.string.game_register)) },
                        containerColor = Color(0xFF2E3B4E)
                    ) { innerPadding ->
                        MyApp(resultViewModel, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
private fun MyApp(resultsViewModel: ResultViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val results by resultsViewModel.gameResults.collectAsState(initial = emptyList())
    var selectedGame by rememberSaveable  { mutableStateOf<GameResult?>(null) }

    LazyColumn(
        modifier = modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    )  {
        items(results) { result ->
            InformationCard(result) { goToDetailReg(context, result) }
        }
    }
}

@Composable
private fun TabletDisplay(resultsViewModel: ResultViewModel,modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val results by resultsViewModel.gameResults.collectAsState(initial = emptyList())
    var selectedGame by rememberSaveable  { mutableStateOf<GameResult?>(null) }

    Row(modifier = modifier.fillMaxWidth().padding(10.dp)) {

        Column (
            modifier = Modifier.weight(1f).padding(10.dp),
        ) {
            Text(
                text = stringResource(R.string.game_list),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 10.dp)
            )
            LazyColumn(

                verticalArrangement = Arrangement.spacedBy(12.dp)
            )  {
                items(results) { result ->
                    InformationCard(result) { selectedGame = result }
                }
            }
        }


        Column(modifier = Modifier.weight(1f).padding(10.dp)) {
            Text(
                text = stringResource(R.string.game_information),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 10.dp)
            )
            GameDetail(selectedGame)
        }
    }




}

@Composable
private fun InformationCard(result: GameResult, onClick: () -> Unit) {
    val backgroundColor: Color
    val label: String
    if (result.userTeam == result.winnerTeam) {
        backgroundColor = Color(0xFFD0F0C0)
        label = stringResource(R.string.win)
    } else {
       backgroundColor = Color(0xFFFFCDD2)
        label = stringResource(R.string.lose)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            backgroundColor
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = result.result, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stringResource(R.string.game_player, result.alias), style = MaterialTheme.typography.bodyMedium)
                Text(text = stringResource(R.string.game_date, result.date), style = MaterialTheme.typography.bodySmall)
            }


            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = label.uppercase(),
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = -30f
                            alpha = 0.30f
                        },
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Gray
                )
            }
        }

    }
}

@Composable
private fun GameDetail(gameResult: GameResult?) {

    if (gameResult != null) {

        val resultColor = Color(0xFFEEEEEE)

        Column (
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            Card(
                colors = CardDefaults.cardColors(containerColor = resultColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.game_winner, gameResult.winnerTeam),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )

                        Text(
                            text = stringResource(R.string.game_player, gameResult.alias),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
                        )

                        Text(
                            text = stringResource(R.string.game_date, gameResult.date),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        InfoRow(stringResource(R.string.game_player_team), gameResult.userTeam)
                        InfoRow(stringResource(R.string.game_cpu_team), if (gameResult.userTeam == "WHITE") "BLACK" else "WHITE")
                        InfoRow(stringResource(R.string.game_user_pieces), gameResult.userPieces.toString())
                        InfoRow(stringResource(R.string.game_cpu_pieces), gameResult.cpuPieces.toString())
                        InfoRow(stringResource(R.string.game_movements), gameResult.totalMovements.toString())
                        InfoRow(stringResource(R.string.game_time_deadline), if (gameResult.timeDeadLine) stringResource(R.string.yes) else stringResource(R.string.no))
                        InfoRow(stringResource(R.string.game_leftover_time), if(gameResult.timeDeadLine) gameResult.leftOverTime else stringResource(R.string.game_no_leftover))
                    }
                }
            }
        }
    }

}
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}

private fun goToDetailReg(context: Context, result: GameResult) {
    val intent = Intent(context, DetailReg::class.java)
    intent.putExtra("result", result)
    context.startActivity(intent)
}