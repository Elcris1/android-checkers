package com.example.checkers

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
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
fun emptyBoard(): SnapshotStateList<SnapshotStateList<String>> {
    return remember {
        mutableStateListOf<SnapshotStateList<String>>().apply {
            repeat(8) { row ->
                val rowList = mutableStateListOf<String>()
                repeat(8) { col ->
                    if ((row + col) % 2 == 0 && (row <= 2 || row >= 5)) {
                        rowList.add(if (row <= 2) "Black" else "White")
                    } else {
                        rowList.add("")
                    }

                }
                add(rowList)
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
    val flag by game.onGoing.collectAsState()
    val loading by game.loading.collectAsState()


    Column () {


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
    }
    if (loading) {
        CircularProgressIndicator()

    }
    if (!flag) {
        Text(modifier = modifier.padding(top = 200.dp), text = game.endingMessage)
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