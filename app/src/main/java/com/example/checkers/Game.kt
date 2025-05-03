package com.example.checkers

import android.util.Log
import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.util.packInts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Game: ViewModel() {
    private val board = Board().apply { createStartingBoard() }
    private val _cells = MutableStateFlow(board.getBoard())
    val cells: StateFlow<Array<Array<Cell>>> = _cells

    // number of pieces alive
    var blackCount = 12
    var whiteCount = 12

    var turn: Teams = Teams.BLACK

    //TODO: player may chose team
    var playerTeam: Teams = Teams.WHITE

    lateinit var selectedCell: Position


    //CPU delay interaction
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    //Ending game
    private val _onGoing = MutableStateFlow(true)
    val onGoing: StateFlow<Boolean> = _onGoing
    private val _mensaje = MutableSharedFlow<String>()
    val mensaje: SharedFlow<String> = _mensaje

    //Force kill user rule
    var forceKill = false
    var availablePieces: MutableList<Piece> = mutableListOf()


    private fun updateBoard() {
        val nuevaMatriz = Array(10) { Array(10) { Cell.fromCode('X') } } // Nueva matriz vacía
        nuevaMatriz.forEachIndexed { y, fila ->
            fila.forEachIndexed { x, _ ->
                nuevaMatriz[y][x] = board.getCell(x, y) // Copiar datos
            }
        }
        _cells.value = nuevaMatriz
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

        if(board.movePiece(selectedCell.x, selectedCell.y, x, y)) {
            //if true is a killer movement
            val  cell = board.getCell(x, y)
            if (turn == Teams.WHITE) blackCount--
            if(turn == Teams.BLACK) whiteCount--
            board.unMarkPossibleMovements()
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
            calculateKillingMovements()
        }
    }

    private fun computerTurn(){
        val pieces = if (playerTeam == Teams.WHITE) board.blackPieces else board.whitePieces
        var availablePieces: MutableList<Piece> = mutableListOf()
        for (piece in pieces) {
            if (board.canKill(piece.position.x, piece.position.y, piece)) {
                availablePieces = mutableListOf()
                availablePieces.add(piece)
                Log.d("Computer turn", "KIll movement in $piece")
                break
            }
            val movements = board.showPossibleMovement(piece.position.x, piece.position.y, piece.team)
            if (movements > 0) {
                availablePieces.add(piece)
                Log.d("Adding possible pieces", "$piece")
            }
        }
        board.unMarkPossibleMovements()


        viewModelScope.launch {
            try {
                _loading.value = true
                delay(1000)
                _loading.value = false
                val selectedPiece = availablePieces.random()
                selectedCell = selectedPiece.position
                board.showPossibleMovement(selectedPiece.position.x, selectedPiece.position.y, selectedPiece.team)
                val selectedMovement = board.modifiedCells.random()
                movePiece(selectedMovement.position.x, selectedMovement.position.y)
            } catch (error: NoSuchElementException) {
                gameEnds("Computer has no movements", "USER WINS!")
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
    private fun gameEnds(msg: String, winner: String): Boolean {
        Log.d("GAME ANNOUNCER", msg)
        viewModelScope.launch {
            _mensaje.emit(winner)
        }
        _onGoing.value = false
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