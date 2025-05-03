package com.example.checkers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkers.ui.theme.CheckersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckersTheme {
                Surface (modifier = Modifier.fillMaxSize().padding(20.dp),
                    color = MaterialTheme.colorScheme.background)
                {
                    MyApp(game = Game())

                }
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier, game: Game) {

    //TODO: que el jugador pugui elegir amb quines fitxes jugar.
    LaunchedEffect(Unit) {
        game.firstTurn(Teams.WHITE)

    }
    val cells by game.cells.collectAsState()
    val onGoing by game.onGoing.collectAsState()
    val loading by game.loading.collectAsState()
    val endingMessage by game.mensaje.collectAsState(initial = "")
    var showDialog by remember { mutableStateOf(false) }


    Column (horizontalAlignment = Alignment.CenterHorizontally){
        if (loading) {
            CircularProgressIndicator(
                modifier =
            modifier.size(50.dp)
            )

        }
    }


    LaunchedEffect(endingMessage) {
        if (endingMessage.isNotEmpty()) {
            showDialog = true
        }
    }

    if (showDialog) {
        ShowDialog(endingMessage) { showDialog = false }
    }

    Column (modifier = modifier.padding(top = 100.dp)) {

        Text("Checkers app")
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
                                else Color(0xFFD0A43C) )
                    )
                    {
                        if(cell.isFilled() || cell.isPossibleMovement()) {
                            val pieceColor = when {
                                cell.isPossibleMovement() -> Color.Red
                                cell.piece?.team == Teams.BLACK -> Color.Black
                                cell.piece?.team == Teams.WHITE -> Color.White
                                else -> Color.Transparent
                            }

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
                                    contentDescription = "Ficha",
                                    modifier = Modifier.size(60.dp)
                                        .clickable(enabled = (cell.piece != null && cell.piece!!.team == game.turn && !game.forceKill)
                                            || cell.isPossibleMovement()
                                                || game.forceKill && game.availablePieces.contains(cell.piece) ) {
                                        //TODO: Check Turn
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
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text("Black: ${game.blackCount}")

            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.DarkGray)
                    .padding(8.dp)
            ) {
                Text("White: ${game.whiteCount}", color = Color.White)
            }

        }
        if (!onGoing) {
            Text(modifier = modifier.padding(top = 200.dp), text = endingMessage)
        }


    }

}
@Composable
fun ShowDialog(endingMessage: String, onDismiss: () -> Unit) {
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CheckersTheme {
        val positions = MutableList(8) {MutableList(8){""} }
        for( i in 0..7){
            for(j in 0..7){
                if ((i + j) % 2 == 0 && (i <= 2 || i >= 5)) {
                    val value = if (i <= 2) "Black" else "White"
                    positions[i][j] = value
                }
            }
        }

    }
}