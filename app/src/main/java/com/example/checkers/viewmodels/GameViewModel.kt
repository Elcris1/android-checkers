package com.example.checkers.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkers.data.Board
import com.example.checkers.data.Cell
import com.example.checkers.data.GameLog
import com.example.checkers.data.Piece
import com.example.checkers.data.Position
import com.example.checkers.data.constants.Teams
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class GameViewModel : ViewModel() {

    init {
        Log.d("GameViewModel", "ViewModel inicializado")
    }

    private val board = Board().apply { createStartingBoard() }
    val cells: MutableState<Array<Array<Cell>>> = mutableStateOf(board.cells)

    // number of pieces alive
    private var blackCount = 12
    private var whiteCount = 12

    //turns
    private var turn: Teams = Teams.BLACK
    private var turnCount = 0
    var playerTeam: Teams = Teams.WHITE
    lateinit var selectedCell: Position


    //CPU delay interaction
    val loading: MutableState<Boolean> = mutableStateOf(false)
    //Ending game
    private val mensaje: MutableState<String> = mutableStateOf("")

    //Force kill user rule
    private var forceKill = false
    private var availablePieces: MutableList<Piece> = mutableListOf()

    //Game logs
    private val logs = mutableStateListOf<GameLog>()

    fun getBlackCount(): Int {
        return blackCount
    }

    fun getWhiteCount(): Int {
        return whiteCount
    }

    fun getTurnCount(): Int {
        return turnCount
    }

    fun getActualTurn(): Teams {
        return turn
    }

    fun isLoading(): Boolean {
        return loading.value
    }

    fun getEndMessage(): String {
        return mensaje.value
    }

    fun getAvailablePieces(): MutableList<Piece> {
        return availablePieces
    }

    fun isForceKill(): Boolean {
        return forceKill
    }

    fun getLogMessages() : MutableList<GameLog> {
        return logs
    }

    fun addLog(gameLog: GameLog) {
        logs.add(gameLog)
    }

    fun clearLogs() {
        logs.clear()
    }

    private fun reduceBlackCount() {
        blackCount--
        addLog(GameLog(GameLog.NUMBER_OF_BLACK, blackCount.toString()))
    }

    private fun reduceWhiteCount() {
        whiteCount--
        addLog(GameLog(GameLog.NUMBER_OF_WHITE, whiteCount.toString()))
    }



    private fun updateBoard() {
        val nuevaMatriz = Array(10) { Array(10) { Cell.fromCode('X') } }
        nuevaMatriz.forEachIndexed { y, fila ->
            fila.forEachIndexed { x, _ ->
                nuevaMatriz[y][x] = board.getCell(x, y)
            }
        }
        cells.value = nuevaMatriz
    }

    fun getNumberUserPieces(): Int {
        if (playerTeam == Teams.WHITE) return whiteCount
        return blackCount
    }
    fun getNumberCPUPieces(): Int {
        if (playerTeam == Teams.WHITE) return blackCount
        return whiteCount
    }

    fun firstTurn(userTeams: Teams) {
        playerTeam = userTeams
        if (userTeams != turn) {
            computerTurn()
        }
    }

    fun showPossibleMovements(x: Int, y: Int, teams: Teams) {
        selectedCell = board.getCell(x, y).position
        board.unMarkPossibleMovements()
        board.showPossibleMovement(x, y, teams)
        updateBoard()
    }

    fun movePiece(x: Int, y: Int){
        turnCount+=1
        if(board.movePiece(selectedCell.x, selectedCell.y, x, y)) {
            //if true is a killer movement
            val  cell = board.getCell(x, y)
            if (turn == Teams.WHITE) reduceBlackCount()
            if(turn == Teams.BLACK) reduceWhiteCount()
            board.unMarkPossibleMovements()

            //Add kill to logs
            addLog(GameLog(GameLog.KILL_MOVEMENT, turn.toString(), selectedCell.toString()))

            //log turn count
            addLog(GameLog(GameLog.TURN, turnCount.toString()))

            if (board.canKill(x, y, cell.piece!!)){
                if (turn == playerTeam) {
                    //mark multi-kill as mandatory
                    forceKill = true
                    return showPossibleMovements(x, y, cell.piece!!.team)
                }
                else {
                    //CPU MULTI-JUMP
                    selectedCell = cell.piece!!.position
                    board.showPossibleMovement(cell.piece!!.position.x, cell.piece!!.position.y, cell.piece!!.team)
                    val selectedJump = board.modifiedCells.random()
                    return movePiece(selectedJump.position.x, selectedJump.position.y)

                }
            }
        } else {
            //add Movement to logs
            addLog(GameLog(GameLog.MOVEMENT, turn.toString(), selectedCell.toString(), Position(x,y).toString()))
            addLog(GameLog(GameLog.TURN, turnCount.toString()))
        }
        forceKill = false
        board.unMarkPossibleMovements()
        updateBoard()
        if(calculateWinner()) return
        changeTurn()

    }

    private fun changeTurn() {
        calculateWinner()
        turn = if (turn == Teams.WHITE) Teams.BLACK else Teams.WHITE
        if(turn != playerTeam) {
            computerTurn()
        } else {
            Log.d("ChangeTurn", "user turn")
            calculateKillingMovements()
        }
    }

    private fun computerTurn(){



        viewModelScope.launch {
            try {
                val pieces = if (playerTeam == Teams.WHITE) board.blackPieces else board.whitePieces
                var availablePieces: MutableList<Piece> = mutableListOf()
                for (piece in pieces) {
                    Log.d("Calculating movements", "${board.canKill(piece.position.x, piece.position.y, piece)}, $piece" )
                    if (board.canKill(piece.position.x, piece.position.y, piece)) {
                        availablePieces = mutableListOf()
                        availablePieces.add(piece)
                        break
                    }
                    val movements = board.showPossibleMovement(piece.position.x, piece.position.y, piece.team)
                    if (movements > 0) {
                        availablePieces.add(piece)
                    }
                }
                board.unMarkPossibleMovements()
                loading.value = true
                delay(1000)
                loading.value = false
                val selectedPiece = availablePieces.random()
                selectedCell = selectedPiece.position
                board.showPossibleMovement(selectedPiece.position.x, selectedPiece.position.y, selectedPiece.team)
                val selectedMovement = board.modifiedCells.random()
                movePiece(selectedMovement.position.x, selectedMovement.position.y)
            } catch (error: NoSuchElementException) {
                val winner = if (playerTeam == Teams.WHITE) "WHITE WINS!" else "BLACK WINS!"
                gameEnds("Computer has no movements", winner)
            }

        }



    }

    private fun calculateWinner(): Boolean {
        if (whiteCount == 0) return gameEnds("WHITE has no pieces", "BLACK WINS!")
        if (blackCount == 0) return gameEnds("BLACK has no pieces", "WHITE WINS!")
        //val pieces = if (playerTeam == Teams.WHITE) board.whitePieces else board.blackPieces
        for(piece in board.whitePieces) {
            if (board.showPossibleMovement(piece.position.x, piece.position.y, piece.team) > 0) {
                board.unMarkPossibleMovements()
                return false
            }
        }
        for(piece in board.blackPieces) {
            if (board.showPossibleMovement(piece.position.x, piece.position.y, piece.team) > 0) {
                board.unMarkPossibleMovements()
                return false
            }
        }
        return true
    }
    fun gameEnds(msg: String, winner: String): Boolean {
        Log.d("GAME ANNOUNCER", msg)
        val cpuTeam = if (playerTeam == Teams.WHITE) Teams.BLACK.toString() else Teams.WHITE.toString()

        var teamWinner = if (winner == "WHITE WINS!") Teams.WHITE.toString() else Teams.BLACK.toString()
        if (winner == "CPU") {
            teamWinner = cpuTeam
        }
        addLog(GameLog(GameLog.VICTORY, teamWinner))

        viewModelScope.launch {
            mensaje.value = winner
        }
        return true
    }
    private fun calculateKillingMovements(){
        val pieces = if (playerTeam == Teams.WHITE) board.whitePieces else board.blackPieces
        val availablePieces: MutableList<Piece> = mutableListOf()
        for (piece in pieces) {
            if (board.canKill(piece.position.x, piece.position.y, piece)) {
                availablePieces.add(piece)
            }
        }
        if(availablePieces.size > 0) {
            forceKill = true
            this.availablePieces = availablePieces
        }

    }


}