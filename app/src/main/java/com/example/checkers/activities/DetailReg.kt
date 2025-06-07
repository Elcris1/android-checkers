package com.example.checkers.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.checkers.R
import com.example.checkers.activities.ui.theme.CheckersTheme
import com.example.checkers.components.GoBackAppBar
import com.example.checkers.data.local.entity.GameResult
import com.example.checkers.viewmodels.ResultViewModel

class DetailReg : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val result  = intent.getSerializableExtra("result") as GameResult
        setContent {

            CheckersTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { GoBackAppBar(stringResource(R.string.game_details)) },
                    containerColor = Color(0xFF2E3B4E)
                ) { innerPadding ->
                    MyApp(result, Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    private fun MyApp(result: GameResult, modifier: Modifier = Modifier){

        GameDetail(result,  modifier)

    }

    @Composable
    private fun GameDetail(gameResult: GameResult, modifier: Modifier = Modifier) {

        val resultColor: Color
        val label: String
        if (gameResult.userTeam == gameResult.winnerTeam) {
            resultColor = Color(0xFFD0F0C0)
            label = stringResource(R.string.win)
        } else {
            resultColor = Color(0xFFFFCDD2)
            label = stringResource(R.string.lose)
        }


        Column (
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            Card(
                colors = CardDefaults.cardColors(containerColor = resultColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(10.dp)
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


    }
}



