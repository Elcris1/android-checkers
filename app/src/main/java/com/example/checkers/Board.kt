package com.example.checkers

import android.inputmethodservice.Keyboard.Row
import java.nio.channels.NotYetBoundException

class Board {
    lateinit var cells: Array<Array<Cell>>
    // var that enables to know if user is in the middle of movement.
    var is_piece_selected: Boolean = false
    //TODO: list of marked as possible option, cells of which state is not changed should become original state

    //TODO: list of alive pieces -> check existence of killability, calculate al possible movements to select one at random.

    fun createStartingBoard() {
        cells = Array(10){Array(10) {Cell(type = CellType.FORBIDDEN, state = CellState.EMPTY)} }

        for (y in 0..9) {
            for(x in 0..9) {

                val cell = cells[y][x]
                val pos = Position(x,y)
                cell.position = pos
                if (x!=0 && x!=9 && y!=0 && y!=9 ) {
                    // SET COLOR OF THE CELL
                    if((y+x) % 2 == 0) {
                        cell.type = CellType.BROWN
                    } else {
                        cell.type = CellType.YELLOW
                    }
                    //SET PIECE OF THE CELL
                    if((x + y) % 2 == 0 && (y <= 3 || y >= 6)) {
                        if(y<=3) cell.placePiece(piece = Piece(PieceType.PAWN, Teams.BLACK, pos))
                        else cell.placePiece(piece = Piece(PieceType.PAWN, Teams.WHITE, pos))
                    }
                }

            }
        }

    }

    fun createBoardFromString(input: String) {
        cells = Array(10){Array(10) {Cell(type = CellType.FORBIDDEN, state = CellState.EMPTY)} }
        require(input.length == 100) { "El string debe tener exactamente 100 caracteres para un tablero 10x10." }

        cells = Array(10) { row ->
            Array(10) { col ->
                val char = input[row * 10 + col]
                val cell = Cell.fromCode(char)
                cell.changePosition(col, row)
                cell
            }
        }

    }

    fun getBoard(): Array<Array<Cell>> {
        return cells
    }
    fun getCell(x: Int, y: Int) : Cell {
        if(!::cells.isInitialized) {
            throw NotYetBoundException()
        }
        return cells[y][x]
    }

    fun placePiece(x: Int, y: Int, piece: Piece) {
        getCell(x, y).placePiece(piece)
    }

    fun removePiece(x: Int, y: Int) {
        getCell(x,y).removePiece()
    }

    fun showBoard() {
        for(row in cells){
            print("|")
            for(cell in row) {
                System.out.printf(" %6s |", cell)
            }
            print("\n")
            //Log.d("Tablero:", row.toString())

        }
    }

    fun showPossibleMovement(x: Int, y: Int, teams: Teams = Teams.WHITE) {
        //TODO: Finish test
        val piece = cells[y][x].piece!!
        //if is dame => dame movements
        if(piece.isDame()){
            return showPossibleDameMovements(x, y, piece)
        }
        //if can kill -> show kill ability
        if(canKill(x, y, piece)) {
            return showKillableMoves(x, y, piece)
        }

        //Mark possible moves
        val leftCell = getDiagonalLeft(x, y, teams)
        if (leftCell.isEmpty()) {
            leftCell.markAsPossible()
        }
        val rightCell = getDiagonalRight(x,y, teams)
        if (rightCell.isEmpty()) {
            rightCell.markAsPossible()
        }


    }

    fun showPossibleDameMovements(x: Int, y: Int, piece: Piece) {
        //TODO: test
        if(canKill(x, y, piece)) {
            return showKillableDameMoves(x,y,piece)
        }
        //Superior diagonals
        //Get diagonalLeft
        var cell = getDiagonalLeft(x, y, Teams.WHITE)
        if (cell.isEmpty()) {
            cell.markAsPossible()
        }
        //Get diagonalRight
        cell = getDiagonalRight(x,y, Teams.WHITE)
        if (cell.isEmpty()) {
            cell.markAsPossible()
        }

        //InferiorDiagonals
        //DiagonalLeft
        cell = getDiagonalLeft(x,y, Teams.BLACK)
        if (cell.isEmpty()) {
            cell.markAsPossible()
        }

        //DiagonalRight
        cell = getDiagonalRight(x,y, Teams.BLACK)
        if (cell.isEmpty()) {
            cell.markAsPossible()
        }
    }

    fun showKillableMoves(x: Int, y: Int, piece: Piece) {
        //TODO: test
        if(isDiagonalLeftKillable(x, y, piece)) {
            getFarDiagonalLeft(x, y, piece.team).markAsKillablePossible()
        }
        if(isDiagonalRightKillable(x, y, piece)) {
            getFarDiagonalRight(x,y, piece.team).markAsKillablePossible()
        }
    }
    fun showKillableDameMoves(x: Int, y: Int, piece: Piece) {
        //TODO: test
        if(isDiagonalLeftKillable(x, y, piece, Teams.WHITE)) {
            getFarDiagonalLeft(x, y, Teams.WHITE).markAsKillablePossible()
        }
        if (isDiagonalLeftKillable(x, y, piece, Teams.BLACK)){
            getFarDiagonalLeft(x,y,Teams.BLACK).markAsKillablePossible()
        }
        if (isDiagonalRightKillable(x, y, piece, Teams.WHITE)){
            getFarDiagonalRight(x,y,Teams.WHITE).markAsKillablePossible()
        }
        if (isDiagonalRightKillable(x, y, piece, Teams.BLACK)){
            getFarDiagonalRight(x,y,Teams.BLACK).markAsKillablePossible()
        }
    }


    fun isDiagonalLeftKillable(x: Int, y: Int, piece: Piece, team: Teams = piece.team): Boolean {
        //This functions can be passed with parameter piece and team
        //this means if only piece is passed team and piece.team are equals
        //if it is passed with piece and team it most likely means piece.team != team
        //if piece.team and team are different means is being used by dame
        //if they are equal means is being used by pawn and the original move of dame
        if ((team == Teams.WHITE && y<=2) || (team == Teams.BLACK && y>=7) || (x<=2) ) return false
        val diagonalLeft = getDiagonalLeft(x, y, team)
        val farLeft = getFarDiagonalLeft(x,y, team)
        return (diagonalLeft.isFilled() && diagonalLeft.piece!!.team != piece.team && farLeft.isEmpty())
    }
    fun isDiagonalRightKillable(x: Int, y: Int, piece: Piece, team: Teams = piece.team): Boolean {
        //This functions can be passed with parameter piece and team
        //this means if only piece is passed team and piece.team are equals
        //if it is passed with piece and team it most likely means piece.team != team
        //if piece.team and team are different means is being used by dame
        //if they are equal means is being used by pawn and the original move of dame

        if ((team == Teams.WHITE && y<=2) || (team == Teams.BLACK && y>=7) || (x>=7) ) return false
        val diagonalRight = getDiagonalRight(x,y, team)
        val farRight =  getFarDiagonalRight(x,y, team)
        return (diagonalRight.isFilled() && diagonalRight.piece!!.team != piece.team && farRight.isEmpty())
    }

    fun getDiagonalLeft(x: Int, y: Int, teams: Teams = Teams.WHITE): Cell {
        if(teams == Teams.BLACK) {
            return getCell(x-1, y+1)
        }
        return getCell(x-1, y-1)

    }
    fun getFarDiagonalLeft(x: Int, y: Int, teams: Teams = Teams.WHITE) : Cell {
        if(teams == Teams.BLACK) {
            return getCell(x-2, y+2)
        }
        return getCell(x-2, y-2)

    }

    fun getDiagonalRight(x: Int, y: Int, teams: Teams = Teams.WHITE): Cell {
        if(teams == Teams.BLACK) {
            return getCell(x+1,y+1)
        }
        return getCell(x+1, y-1)
    }
    fun getFarDiagonalRight(x: Int, y: Int, teams: Teams = Teams.WHITE): Cell {
        if (teams == Teams.BLACK){
            return getCell(x+2, y+2)
        }
        return getCell(x+2, y-2)
    }



    fun canKill(x: Int, y: Int, piece: Piece) : Boolean{
        if(piece.isDame()) {

            return isDiagonalLeftKillable(x, y, piece, Teams.WHITE) || isDiagonalLeftKillable(x, y, piece, Teams.BLACK) ||
                    isDiagonalRightKillable(x, y, piece, Teams.WHITE) || isDiagonalRightKillable(x, y, piece, Teams.BLACK)
        } else {
            return isDiagonalRightKillable(x, y, piece) || isDiagonalLeftKillable(x, y, piece)
        }
    }

    override fun equals(other: Any?): Boolean {
        if(this===other) return true
        if(other !is Board) return false
        for (y in 0..9){
            for(x in 0..9){
                val thisCell = getCell(x,y)
                val otherCell = other.getCell(x,y)

                if (!thisCell.equals(otherCell)) {
                    return false
                }
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 0;
        for (y in 0..9){
            for (x in 0..9){
                result += 7 * y * x * cells[y][x].hashCode()
            }
        }
        return result
    }
}