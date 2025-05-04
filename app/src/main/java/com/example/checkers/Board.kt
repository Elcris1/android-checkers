package com.example.checkers

import android.util.Log
import java.nio.channels.NotYetBoundException

class Board {
    lateinit var cells: Array<Array<Cell>>
    var modifiedCells: MutableList<Cell> = mutableListOf()
    var blackPieces: MutableList<Piece> = mutableListOf()
    var whitePieces: MutableList<Piece> = mutableListOf()
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
                        var piece: Piece
                        if(y<=3) {
                            piece = Piece(PieceType.PAWN, Teams.BLACK, pos)
                            cell.placePiece(piece = piece)
                            blackPieces.add(piece)
                        }
                        else {
                            piece = Piece(PieceType.PAWN, Teams.WHITE, pos)
                            cell.placePiece(piece = piece)
                            whitePieces.add(piece)
                        }
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

    fun showPossibleMovement(x: Int, y: Int, teams: Teams = Teams.WHITE) : Int{
        val piece = cells[y][x].piece!!
        //if is dame => dame movements
        if(piece.isDame()){
            return showPossibleDameMovements(x, y, piece)
        }
        //if can kill -> show kill ability
        if(canKill(x, y, piece)) {
            return showKillableMoves(x, y, piece)
        }
        var possibilities = 0
        //Mark possible moves
        val leftCell = getDiagonalLeft(x, y, teams)
        if (leftCell.isEmpty()) {
            leftCell.markAsPossible()
            modifiedCells.add(leftCell)
            possibilities+=1
        }
        val rightCell = getDiagonalRight(x,y, teams)
        if (rightCell.isEmpty()) {
            rightCell.markAsPossible()
            modifiedCells.add(rightCell)
            possibilities+=1
        }
        return possibilities


    }

    fun showPossibleDameMovements(x: Int, y: Int, piece: Piece): Int {
        //TODO: test
        if(canKill(x, y, piece)) {
            return showKillableDameMoves(x,y,piece)
        }
        //Superior diagonals
        //Get diagonalLeft
        var possibilities = 0
        var cell = getDiagonalLeft(x, y, Teams.WHITE)
        if (cell.isEmpty()) {
            cell.markAsPossible()
            modifiedCells.add(cell)
            possibilities += 1
        }
        //Get diagonalRight
        cell = getDiagonalRight(x,y, Teams.WHITE)
        if (cell.isEmpty()) {
            cell.markAsPossible()
            modifiedCells.add(cell)
            possibilities += 1
        }

        //InferiorDiagonals
        //DiagonalLeft
        cell = getDiagonalLeft(x,y, Teams.BLACK)
        if (cell.isEmpty()) {
            cell.markAsPossible()
            modifiedCells.add(cell)
            possibilities += 1

        }

        //DiagonalRight
        cell = getDiagonalRight(x,y, Teams.BLACK)
        if (cell.isEmpty()) {
            cell.markAsPossible()
            modifiedCells.add(cell)
            possibilities += 1
        }
        return possibilities
    }

    fun showKillableMoves(x: Int, y: Int, piece: Piece): Int {
        var cell: Cell
        var possibilities = 0
        if(isDiagonalLeftKillable(x, y, piece)) {
            cell = getFarDiagonalLeft(x, y, piece.team)
            Log.d("white piece", "diagonal left should be killable")
            cell.markAsKillablePossible()
            modifiedCells.add(cell)
            possibilities += 1
        }
        if(isDiagonalRightKillable(x, y, piece)) {
            cell = getFarDiagonalRight(x,y, piece.team)
            cell.markAsKillablePossible()
            modifiedCells.add(cell)
            possibilities += 1
        }
        return possibilities
    }
    fun showKillableDameMoves(x: Int, y: Int, piece: Piece): Int {
        var cell: Cell
        var possibilities = 0;
        if(isDiagonalLeftKillable(x, y, piece, Teams.WHITE)) {
            cell = getFarDiagonalLeft(x, y, Teams.WHITE)
            cell.markAsKillablePossible()
            modifiedCells.add(cell)
            possibilities += 1
        }
        if (isDiagonalLeftKillable(x, y, piece, Teams.BLACK)){
            cell = getFarDiagonalLeft(x,y,Teams.BLACK)
            cell.markAsKillablePossible()
            modifiedCells.add(cell)
            possibilities += 1
        }
        if (isDiagonalRightKillable(x, y, piece, Teams.WHITE)){
            cell = getFarDiagonalRight(x,y,Teams.WHITE)
            cell.markAsKillablePossible()
            modifiedCells.add(cell)
            possibilities += 1
        }
        if (isDiagonalRightKillable(x, y, piece, Teams.BLACK)){
            cell = getFarDiagonalRight(x,y,Teams.BLACK)
            cell.markAsKillablePossible()
            modifiedCells.add(cell)
            possibilities += 1
        }
        return possibilities
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
        if (diagonalLeft.piece != null) {
            Log.d("DiagonalLeft", "DIF COLOR: ${diagonalLeft.piece!!.team != piece.team}, $diagonalLeft, $farLeft,  Result: ${(diagonalLeft.isFilled() && diagonalLeft.piece!!.team != piece.team && farLeft.isEmpty())}")
        }
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
        if (diagonalRight.piece != null) {
            Log.d("DiagonalRight", "DIF COLOR: ${diagonalRight.piece!!.team != piece.team}, ${diagonalRight}, ${farRight}, Result: ${(diagonalRight.isFilled() && diagonalRight.piece!!.team != piece.team && farRight.isEmpty())}")
        }
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
        }
        return isDiagonalRightKillable(x, y, piece) || isDiagonalLeftKillable(x, y, piece)

    }

    fun movePiece(xOr: Int, yOr: Int,  xDest: Int, yDest: Int): Boolean {
        val origin = getCell(xOr, yOr)
        val dest = getCell(xDest, yDest)
        val killed = dest.isKillMovement()
        if (killed) {
            killMovement(origin, dest)
        }
        val piece = origin.removePiece()
        origin.setEmpty()
        dest.placePiece(piece!!)
        checkPromotion(piece)
        return killed
    }

    private fun killMovement(origin: Cell, dest: Cell) {
        val piece = getMiddleCell(origin, dest).removePiece()
        if (piece!!.team == Teams.BLACK) blackPieces.remove(piece)
        if (piece.team == Teams.WHITE) whitePieces.remove(piece)
    }

    private fun getMiddleCell(origin: Cell, dest: Cell): Cell{
        val x = (dest.position.x - origin.position.x)/2 + origin.position.x
        val y = (dest.position.y - origin.position.y)/2 + origin.position.y
        return getCell(x,y)
    }
    private fun checkPromotion(piece: Piece) {
        if(piece.team == Teams.WHITE && piece.position.y == 1 ||
            piece.team == Teams.BLACK && piece.position.y == 8) piece.convertToDame()
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

    fun unMarkPossibleMovements() {
        for (cell in modifiedCells) {
            if(cell.piece == null) {
                cell.setEmpty()
            } else {
                cell.setFilled()
            }
        }
        modifiedCells = mutableListOf()
    }
}