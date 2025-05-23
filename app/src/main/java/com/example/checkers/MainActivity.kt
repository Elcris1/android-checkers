package com.example.checkers

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.collection.objectFloatMapOf
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.checkers.ui.theme.CheckersTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alias = intent.getStringExtra("alias") ?: "Player"
        val whiteTeam = intent.getBooleanExtra("whiteTeam", true)
        val timeDeadline = intent.getBooleanExtra("timeDeadline", false)
        val minuteLimit = intent.getIntExtra("minuteLimit", 0)
        val secondLimit = intent.getIntExtra("secondLimit", 0)
        val game: Game by viewModels()

        enableEdgeToEdge()
        setContent {

            CheckersTheme {
                Surface (modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2E3B4E))
                {
                    MyApp(
                        alias = alias,
                        whiteTeam = whiteTeam,
                        timeDeadline = timeDeadline,
                        minuteLimit = minuteLimit,
                        secondLimit = secondLimit,
                        game = game
                    )

                }
            }
        }
    }
}


@Composable
private fun MyApp(
    modifier: Modifier = Modifier,
    alias: String,
    whiteTeam: Boolean,
    timeDeadline: Boolean,
    minuteLimit: Int,
    secondLimit: Int,
    game: Game
) {
    val team = if (whiteTeam) Teams.WHITE else Teams.BLACK


    LaunchedEffect(Unit) {
        game.firstTurn(team)
    }

    val context = LocalContext.current

    val endingMessage = game.mensaje
    var showDialog by remember { mutableStateOf(false) }
    val remainingTime = rememberSaveable { mutableIntStateOf((minuteLimit * 60 + secondLimit)) }
    val stopTimer = rememberSaveable{ mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    if(!isLandscape){
        val loading = game.loading
        LoadingIndicator(loading, modifier = Modifier.padding(top = 16.dp))
    }

    Column(modifier=  modifier.fillMaxSize()) {

        LaunchedEffect(endingMessage.value) {
            if (endingMessage.value.isNotBlank()) {
                showDialog = true
            }
        }

        if (showDialog) {
            ShowDialog(endingMessage.value, team, alias,  timeDeadline, remainingTime, stopTimer, game, context)
        }

        BoardScreen(isLandscape, timeDeadline, remainingTime, stopTimer, whiteTeam, alias, game)
    }

}
@Composable
private fun BoardScreen(
    isLandscape: Boolean,
    timeDeadline: Boolean,
    remainingTime: MutableState<Int>,
    stopTimer: MutableState<Boolean>,
    whiteTeam: Boolean,
    alias: String,
    game: Game,
    modifier: Modifier = Modifier,
) {
    val cells = game.cells
    if (isLandscape) {
        HorizontalBoard(timeDeadline, remainingTime, stopTimer, whiteTeam, alias, cells, game)
    } else {
        VerticalBoard(timeDeadline, remainingTime, stopTimer, whiteTeam, alias, cells, game)
    }
}
@Composable
private fun VerticalBoard(
    timeDeadline: Boolean,
    remainingTime: MutableState<Int>,
    stopTimer: MutableState<Boolean>,
    whiteTeam: Boolean,
    alias: String,
    cells: MutableState<Array<Array<Cell>>>,
    game: Game,
    modifier: Modifier = Modifier,
)
{
    Column (modifier = modifier.padding(top = 100.dp, start = 8.dp, end = 8.dp)) {

        if(timeDeadline) Timer(remainingTime, stopTimer, onTimeFinish = { onTimeEnd(game) }, modifier)

        GameHeader(whiteTeam, alias)

        for(y in 1..8) {
            Row (modifier.height(50.dp)) {
                for (x in 1..8){
                    val cell = cells.value[y][x]
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
        TurnCounter(game)

    }
}
@Composable
private  fun HorizontalBoard(
    timeDeadline: Boolean,
    remainingTime: MutableState<Int>,
    stopTimer: MutableState<Boolean>,
    whiteTeam: Boolean,
    alias: String,
    cells: MutableState<Array<Array<Cell>>>,
    game: Game,
    modifier: Modifier = Modifier,
) {
    val loading = game.loading
    Row (
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            modifier = modifier
                .weight(2f)
                .padding(top = 20.dp, end = 8.dp, bottom = 16.dp))
        {
            for(y in 1..8) {
                Row (modifier.weight(1f)) {
                    for (x in 1..8){
                        val cell = cells.value[y][x]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
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
        }

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if(timeDeadline) Timer(remainingTime, stopTimer, onTimeFinish = { onTimeEnd(game) }, modifier.padding(end = 8.dp))
            GameHeader(whiteTeam, alias)
            Footer(game)
            TurnCounter(game)
            LoadingIndicator(loading)
        }
    }

}

@Composable
private fun ShowDialog(
    endingMessage: String,
    userTeam: Teams,
    alias: String,
    timeDeadline: Boolean,
    remainingTime: MutableState<Int>,
    shouldStop: MutableState<Boolean>,
    game: Game,
    context: Context
) {
    if (endingMessage.isNotEmpty()) {
        val intent = Intent(context, ResultActivity::class.java)
        val title: String
        var message: String
        if (endingMessage != "CPU") {
            if (endingMessage == "WHITE WINS!") {
                title = stringResource(R.string.white_wins)
                intent.putExtra("result", Teams.WHITE.toString())
            } else {
                title = stringResource(R.string.black_wins)
                intent.putExtra("result", Teams.BLACK.toString())
            }
            message = if (userTeam == Teams.WHITE && endingMessage == "WHITE WINS!" || userTeam == Teams.BLACK && endingMessage == "BLACK WINS!") {
                stringResource(R.string.user_wins, alias, game.turnCount, game.getNumberUserPieces())
            } else {
                stringResource(R.string.cpu_wins, alias, game.turnCount, game.getNumberCPUPieces())
            }
            shouldStop.value = true
            if (timeDeadline) {
                intent.putExtra("time", calcRemainingTime(remainingTime))
                message += "\n" + stringResource(R.string.time_winning, calcRemainingTime(remainingTime))
            }
        } else {
            title = if (userTeam == Teams.WHITE) stringResource(R.string.black_wins) else stringResource(R.string.white_wins)
            message = stringResource(R.string.win_by_time, alias)
            intent.putExtra("time", calcRemainingTime(remainingTime))
            intent.putExtra("result", if(userTeam==Teams.WHITE) Teams.BLACK.toString() else Teams.WHITE.toString())
        }
        Log.d("Enviando datos", calcRemainingTime(remainingTime))
        intent.putExtra("alias", alias)
        intent.putExtra("userTeam", userTeam.toString())
        intent.putExtra("numberPiecesUser", game.getNumberUserPieces())
        intent.putExtra("numberPiecesCPU", game.getNumberCPUPieces())
        intent.putExtra("movements", game.turnCount)

        Dialog(onDismissRequest = { context.startActivity(intent) }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E3A8A), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { context.startActivity(intent) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0A43C))
                    ) {
                        Text(stringResource(R.string.accept), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator(loading: State<Boolean>, modifier: Modifier = Modifier) {

    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        if (loading.value) {
            CircularProgressIndicator(
                modifier = modifier.size(50.dp)
            )

        }
    }
}

private fun calcRemainingTime(remainingTime: MutableState<Int>) : String {
    return "${remainingTime.value/ 60}:${if (remainingTime.value % 60 < 10) "0${remainingTime.value % 60}" else "${remainingTime.value % 60}"}"
}

@Composable
fun Timer(remainingTime: MutableState<Int>, shouldStop: MutableState<Boolean>, onTimeFinish: () -> Unit, modifier: Modifier = Modifier) {

    LaunchedEffect(remainingTime) {
        while (remainingTime.value > 0 && !shouldStop.value) {
            delay(1000L)
            remainingTime.value--
        }
        if(!shouldStop.value) onTimeFinish()
    }

    val time = calcRemainingTime(remainingTime)
    val text = stringResource(R.string.remaining_time) + ": " + time

    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.End,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
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
            .heightIn(min = 50.dp, max = 90.dp)
            .wrapContentHeight()
            .background(Color(0xFF1F2937))
            .shadow(6.dp, shape = RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFD0A43C))
                .fillMaxHeight()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WhiteText(stringResource(R.string.black_team) + ": ${game.blackCount}", 18.sp, Color.Black)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFD0A43C))
                .fillMaxHeight()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WhiteText(stringResource(R.string.white_team) + ": ${game.whiteCount}", 18.sp, Color.Black)
        }
    }
}

@Composable
private fun TurnCounter(game: Game, modifier: Modifier = Modifier) {
    val text = stringResource(R.string.turn_count, game.turnCount)
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.End,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.Start)
    )
}


fun onTimeEnd(game: Game) {
    game.gameEnds("TIME ENDED", "CPU")
}
