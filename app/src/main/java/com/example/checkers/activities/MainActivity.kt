package com.example.checkers.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.checkers.data.Cell
import com.example.checkers.data.constants.CellType
import com.example.checkers.viewmodels.GameViewModel
import com.example.checkers.R
import com.example.checkers.data.GameLog
import com.example.checkers.data.GameResultBuilder
import com.example.checkers.data.constants.Teams
import com.example.checkers.data.local.entity.GameResult
import com.example.checkers.datastore.DataStoreManager
import com.example.checkers.ui.theme.CheckersTheme
import com.example.checkers.viewmodels.ResultViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val game: GameViewModel by viewModels()
        val resultViewModel: ResultViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            CheckersTheme {
                Surface (modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2E3B4E))
                {
                    MyApp(
                        game = game,
                        result = resultViewModel,
                        isTablet = isTablet(windowSizeClass)
                    )

                }
            }
        }
    }
}


@Composable
private fun MyApp(
    modifier: Modifier = Modifier,
    game: GameViewModel,
    result: ResultViewModel,
    isTablet: Boolean
) {

    //DAta from store
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)


    val config by dataStoreManager.configuration.collectAsState(initial = null)

    if (config == null) {
        CircularProgressIndicator()
    } else {
        LoadGame(context, config!!, game, result, isTablet)
    }






}

@Composable
private fun LoadGame(context: Context, config: DataStoreManager.ConfigData, game: GameViewModel, result: ResultViewModel, isTablet: Boolean, modifier: Modifier = Modifier) {


    val alias = config.alias
    val whiteTeam = config.isWhite
    val timeDeadline = config.timeEnabled
    val minuteLimit = config.minutes
    val secondLimit = config.seconds

    val team = if (whiteTeam) Teams.WHITE else Teams.BLACK

    LaunchedEffect(Unit) {
        game.firstTurn(team)
    }


    val endingMessage = game.getEndMessage()
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

        LaunchedEffect(endingMessage) {
            if (endingMessage.isNotBlank()) {
                showDialog = true
            }
        }

        if (showDialog) {
            ShowDialog(endingMessage, team, alias,  timeDeadline, remainingTime, stopTimer, game, result, context)
        }

        BoardScreen(isLandscape, timeDeadline, remainingTime, stopTimer, whiteTeam, alias, game, isTablet)
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
    game: GameViewModel,
    isTablet: Boolean,
    modifier: Modifier = Modifier,
) {
    val cells = game.cells
    if (isLandscape) {
        HorizontalBoard(timeDeadline, remainingTime, stopTimer, whiteTeam, alias, cells, game, isTablet)
    } else {
        VerticalBoard(timeDeadline, remainingTime, stopTimer, whiteTeam, alias, cells, game, isTablet)
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
    game: GameViewModel,
    isTablet: Boolean,
    modifier: Modifier = Modifier,
)
{
    val context = LocalContext.current
    val paddingValues = if(isTablet) PaddingValues(top = 100.dp, start = 25.dp, end = 25.dp) else PaddingValues(top = 100.dp, start = 8.dp, end = 8.dp)
    Column (modifier = modifier.padding(paddingValues)) {

        if(timeDeadline) Timer(remainingTime, stopTimer, onTimeFinish = { onTimeEnd(game) }, game, modifier)

        GameHeader(whiteTeam, alias)

        for(y in 1..8) {
            Row (if(isTablet) modifier.height(80.dp) else modifier.height(50.dp)) {
                for (x in 1..8){
                    val cell = cells.value[y][x]
                    Box(
                        modifier = Modifier
                            .weight(if(isTablet) 0.90f else 1f)
                            .fillMaxSize()
                            .background(
                                if (cell.type == CellType.BROWN) Color(0xFF734222)
                                else Color(0xFFD0A43C)
                            )
                    )
                    {
                        Piece(cell, game, x, y, isTablet)
                    }
                }
            }
        }
        Footer(game)
        TurnCounter(game)

        if (isTablet) {
            WhiteText(stringResource(R.string.logs), fonSize = 24.sp)
            GameLogs(context, game.getLogMessages())
        }

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
    game: GameViewModel,
    isTablet: Boolean,
    modifier: Modifier = Modifier,
) {
    val loading = game.loading
    val context = LocalContext.current
    val paddingValues: PaddingValues = if(isTablet) PaddingValues(top = 50.dp, end = 10.dp, bottom = 50.dp) else PaddingValues(top = 20.dp, end = 8.dp, bottom = 16.dp)
    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        Column (
            modifier = modifier
                .weight(if(isTablet) 1f else 2f)
                .padding(paddingValues)
        ) {
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
                            Piece(cell, game, x, y, isTablet)
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
            if(timeDeadline) Timer(remainingTime, stopTimer, onTimeFinish = { onTimeEnd(game) }, game, modifier.padding(end = 8.dp))
            GameHeader(whiteTeam, alias)
            Footer(game)
            TurnCounter(game)
            if (isTablet) {
                Row (modifier = Modifier.fillMaxWidth()){
                    Column(modifier = Modifier
                        .weight(2f)
                        .padding(8.dp)) {
                        WhiteText(stringResource(R.string.logs), fonSize = 24.sp)
                        GameLogs(context, game.getLogMessages())
                    }
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)) {
                        LoadingIndicator(loading)
                    }
                }

            } else {
                LoadingIndicator(loading)
            }

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
    game: GameViewModel,
    result: ResultViewModel,
    context: Context
) {
    if (endingMessage.isNotEmpty()) {
        val intent = Intent(context, ResultActivity::class.java)
        val title: String
        var message: String
        val gameResultBuilder = GameResultBuilder()

        if (endingMessage != "CPU") {

            //Check which team has won
            if (endingMessage == "WHITE WINS!") {
                title = stringResource(R.string.white_wins)
                intent.putExtra("result", Teams.WHITE.toString())
                gameResultBuilder.winnerTeam = Teams.WHITE.toString()
            } else {
                title = stringResource(R.string.black_wins)
                intent.putExtra("result", Teams.BLACK.toString())
                gameResultBuilder.winnerTeam = Teams.BLACK.toString()
            }

            //Check if user is the winner or the cpu
            message = if (userTeam == Teams.WHITE && endingMessage == "WHITE WINS!" || userTeam == Teams.BLACK && endingMessage == "BLACK WINS!") {
                stringResource(R.string.user_wins, alias, game.getTurnCount(), game.getNumberUserPieces())
            } else {
                stringResource(R.string.cpu_wins, alias, game.getTurnCount(), game.getNumberCPUPieces())
            }
            shouldStop.value = true
            if (timeDeadline) {
                intent.putExtra("time", calcRemainingTime(remainingTime))
                gameResultBuilder.leftOverTime = calcRemainingTime(remainingTime)
                message += "\n" + stringResource(R.string.time_winning, calcRemainingTime(remainingTime))
            }
        } else {
            //if user uns out of time
            title = if (userTeam == Teams.WHITE) stringResource(R.string.black_wins) else stringResource(
                R.string.white_wins
            )
            gameResultBuilder.winnerTeam = if(userTeam == Teams.WHITE) Teams.BLACK.toString() else Teams.WHITE.toString()
            message = stringResource(R.string.win_by_time, alias)
            intent.putExtra("time", calcRemainingTime(remainingTime))
            gameResultBuilder.leftOverTime = calcRemainingTime(remainingTime)
            intent.putExtra("result", if(userTeam== Teams.WHITE) Teams.BLACK.toString() else Teams.WHITE.toString())
        }

        //TODO: add message for more context
        gameResultBuilder.timeDeadLine = timeDeadline
        gameResultBuilder.result = title
        intent.putExtra("alias", alias)
        gameResultBuilder.alias = alias
        intent.putExtra("userTeam", userTeam.toString())
        //TODO: FIX THIS
        gameResultBuilder.userTeam = userTeam.toString()
        intent.putExtra("numberPiecesUser", game.getNumberUserPieces())
        gameResultBuilder.userPieces = game.getNumberUserPieces()
        intent.putExtra("numberPiecesCPU", game.getNumberCPUPieces())
        gameResultBuilder.cpuPieces = game.getNumberCPUPieces()
        intent.putExtra("movements", game.getTurnCount())
        gameResultBuilder.totalMovements = game.getTurnCount()


        result.addGameResult(gameResultBuilder.build())

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
fun Timer(remainingTime: MutableState<Int>, shouldStop: MutableState<Boolean>, onTimeFinish: () -> Unit, game: GameViewModel, modifier: Modifier = Modifier) {

    LaunchedEffect(remainingTime) {
        while (remainingTime.value > 0 && !shouldStop.value) {
            delay(1000L)
            remainingTime.value--
            game.addLog(GameLog(GameLog.REMAINING_TIME, calcRemainingTime(remainingTime)))
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
            val text = stringResource(R.string.CPU)+ " (" + (if(whiteTeam) stringResource(R.string.black_team) else stringResource(
                R.string.white_team
            )) + ")"
            WhiteText(text)
        }
        Column (
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            val text = alias + " (" + (if(!whiteTeam) stringResource(R.string.black_team) else stringResource(
                R.string.white_team
            )) + ")"
            WhiteText(text)
        }
    }
}

@Composable
private fun Piece(cell: Cell, game: GameViewModel, x: Int, y: Int, isTablet: Boolean) {
    if(cell.isFilled() || cell.isPossibleMovement()) {
        val image = when {
            cell.isPossibleMovement() -> R.drawable.possible_movement
            cell.piece!!.isDame() && cell.piece?.team == Teams.BLACK -> R.drawable.black_dame
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
                    .size(if (isTablet) 80.dp else 60.dp)
                    .clickable(
                        enabled = (cell.piece != null && cell.piece!!.team == game.getActualTurn() && !game.isForceKill())
                                || cell.isPossibleMovement()
                                || game.isForceKill() && game.getAvailablePieces().contains(
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
private fun Footer(game: GameViewModel) {
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
            WhiteText(stringResource(R.string.black_team) + ": ${game.getBlackCount()}", 18.sp, Color.Black)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFD0A43C))
                .fillMaxHeight()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WhiteText(stringResource(R.string.white_team) + ": ${game.getWhiteCount()}", 18.sp, Color.Black)
        }
    }
}

@Composable
private fun TurnCounter(game: GameViewModel, modifier: Modifier = Modifier) {
    val text = stringResource(R.string.turn_count, game.getTurnCount())
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

@Composable
private fun GameLogs(context: Context, logs: List<GameLog>) {

    LazyColumn (modifier = Modifier.padding(bottom = 50.dp)) {
        items (logs) { gameLog ->
            WhiteText(GameLog.getString(context, gameLog))
        }
    }


}

fun onTimeEnd(game: GameViewModel) {
    game.addLog(GameLog(GameLog.TIME_LIMIT))
    game.gameEnds("TIME ENDED", "CPU")
}
