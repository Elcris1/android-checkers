package com.example.checkers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkers.ui.theme.CheckersTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alias = intent.getStringExtra("alias") ?: "Jugador"
        val whiteTeam = intent.getBooleanExtra("whiteTeam", true)
        val timeDeadline = intent.getBooleanExtra("timeDeadline", false)
        enableEdgeToEdge()
        setContent {
            CheckersTheme {
                Surface (modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2E3B4E))
                {
                    MyApp(game = Game(), alias = alias, whiteTeam = whiteTeam, timeDeadline = timeDeadline)

                }
            }
        }
    }
}

//TODO: STRINGS OF THIS activity

@Composable
private fun MyApp(modifier: Modifier = Modifier, game: Game, alias: String, whiteTeam: Boolean, timeDeadline: Boolean) {

    LaunchedEffect(Unit) {
        val team = if (whiteTeam) Teams.WHITE else Teams.BLACK
        game.firstTurn(team)
    }

    val cells by game.cells.collectAsState()
    val loading = game.loading.collectAsState()
    val endingMessage by game.mensaje.collectAsState(initial = "")
    var showDialog by remember { mutableStateOf(false) }

    LoadingIndicator(loading)

    LaunchedEffect(endingMessage) {
        if (endingMessage.isNotEmpty()) {
            showDialog = true
        }
    }

    if (showDialog) {
        ShowDialog(endingMessage) { showDialog = false }
    }

    Column (modifier = modifier.padding(top = 100.dp, start = 8.dp, end = 8.dp)) {

        if(timeDeadline) Timer(1, 30, onTimeFinish = { onTimeEnd(game) })

        GameHeader(whiteTeam, alias)

        for(y in 1..8) {
            Row (modifier.height(50.dp)) {
                for (x in 1..8){
                    val cell = cells[y][x]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(
                                if (cell.type == CellType.BROWN) Color(0xFF734222)
                                else Color(0xFFD0A43C)
                            )
                    )
                    {
                        Piece(cell, game, x, y)
                    }
                }
            }
        }
        Footer(game)

    }

}
@Composable
private fun ShowDialog(endingMessage: String, onDismiss: () -> Unit) {
    if (endingMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Mensaje final") },
            text = { Text(endingMessage) },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun LoadingIndicator(loading: State<Boolean>) {

    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        if (loading.value) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp)
            )

        }
    }
}

@Composable
fun Timer(minuteLimit: Int, secondLimit: Int, onTimeFinish: () -> Unit) {
    var remainingTime by remember { mutableStateOf((minuteLimit * 60 + secondLimit)) }

    LaunchedEffect(remainingTime) {
        while (remainingTime > 0) {
            delay(1000L)
            remainingTime--
        }
        onTimeFinish()
    }

    val time = "${remainingTime / 60}:${if (remainingTime % 60 < 10) "0${remainingTime % 60}" else "${remainingTime % 60}"}"
    val text = stringResource(R.string.remaining_time) + ": " + time

    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.End
    )

}

@Composable
private fun WhiteText(text: String, fonSize: TextUnit = 16.sp, color: Color = Color.White) {
    Text(
        text = text,
        fontSize = fonSize,
        color = color,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun GameHeader(whiteTeam: Boolean, alias: String) {
    Row (
        modifier = Modifier.padding(bottom = 5.dp)
    ) {
        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            val text = stringResource(R.string.CPU)+ " (" + (if(whiteTeam) stringResource(R.string.black_team) else stringResource(R.string.white_team)) + ")"
            WhiteText(text)
        }
        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            val text = alias + " (" + (if(!whiteTeam) stringResource(R.string.black_team) else stringResource(R.string.white_team)) + ")"
            WhiteText(text)
        }
    }
}

@Composable
private fun Piece(cell: Cell, game: Game, x: Int, y: Int) {
    if(cell.isFilled() || cell.isPossibleMovement()) {
        val image = when {
            cell.isPossibleMovement() -> R.drawable.possible_movement
            cell.piece!!.isDame() && cell.piece?.team == Teams.BLACK ->  R.drawable.black_dame
            cell.piece!!.isDame() && cell.piece?.team == Teams.WHITE -> R.drawable.white_dame
            cell.piece?.team == Teams.BLACK -> R.drawable.black_pawn
            cell.piece?.team == Teams.WHITE -> R.drawable.white_pawn
            else -> null
        }

        image?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "Piece",
                modifier = Modifier
                    .size(60.dp)
                    .clickable(
                        enabled = (cell.piece != null && cell.piece!!.team == game.turn && !game.forceKill)
                                || cell.isPossibleMovement()
                                || game.forceKill && game.availablePieces.contains(
                            cell.piece
                        )
                    ) {
                        if (cell.isPossibleMovement()) {
                            game.movePiece(x, y)
                        } else {
                            game.showPossibleMovements(x, y, cell.piece!!.team)

                        }

                    }
            )
        }
    }
}

@Composable
private fun Footer(game: Game) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F2937))
            .shadow(6.dp, shape = RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFD0A43C))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WhiteText(stringResource(R.string.black_team) + ": ${game.blackCount}", 18.sp, Color.Black)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFD0A43C))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WhiteText(stringResource(R.string.white_team) + ": ${game.whiteCount}", 18.sp, Color.Black)
        }
    }
}


fun onTimeEnd(game: Game) {
    game.gameEnds("TIME ENDED", "CPU")
}
