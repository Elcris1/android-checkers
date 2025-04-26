package com.example.checkers

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
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
                    MyApp()

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
fun MyApp(modifier: Modifier = Modifier) {
    val positions = emptyBoard()
    for (row in positions) {
        Log.d("Tablero", row.toString())
    }
    var selectedPawn = Pair(-1, -1)
    Column () {
        Text("Checkers app")
        for(i in 0..7) {
            Row (modifier.height(50.dp)) {
                for (j in 0..7){

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(
                                if ((i + j) % 2 == 0) Color(0xFF734222)
                                else Color(0xFFD0A43C) )
                    )
                    {
                        if(positions[i][j] != "") {
                            var pieceColor = Color.Black
                            if( positions[i][j]=="Black"){
                                pieceColor = Color.Black
                            } else if(positions[i][j] == "White") {
                                pieceColor = Color.White
                            } else {
                                pieceColor = Color.Red
                            }

                            Canvas(
                                modifier = Modifier.size(60.dp).clickable {
                                    if(pieceColor == Color.White){
                                        //TODO: REMOVe Red
                                        if(selectedPawn.first != -1) {
                                            removeReds(positions, selectedPawn)
                                        }
                                        showPosibleMovements(positions, i, j)
                                        selectedPawn = Pair(i, j)
                                    }
                                    if(pieceColor == Color.Red) {
                                        //Todo: Mover ficha
                                        movePawn(positions, selectedPawn, Pair(i, j))
                                        //TODO: remove reds

                                    }

                                }
                            ) {
                                drawCircle(color = pieceColor, )
                            }

                        }
                        val text = positions[i][j]
                        Text(text = "($i, $j), $text")
                    }
                }
            }
        }
    }

}

fun showPosibleMovements(positions: SnapshotStateList<SnapshotStateList<String>> , i: Int, j: Int) {
    positions[i-1][j-1] = "Red"
    positions[i-1][j+1] = "Red"
}

fun movePawn(positions: SnapshotStateList<SnapshotStateList<String>>, selectedPawn: Pair<Int, Int>, position: Pair<Int, Int>){
    val (ogI, ogJ) = selectedPawn
    val (finalI, finalJ) = position
    positions[ogI][ogJ] = ""
    positions[finalI][finalJ] = "White"
}

fun removeReds(positions: SnapshotStateList<SnapshotStateList<String>>, previous: Pair<Int, Int>) {
    val (i, j) = previous
    positions[i-1][j-1] = ""
    positions[i-1][j+1] = ""
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