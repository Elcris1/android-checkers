package com.example.checkers

import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.awaitCancellation

class Cell(var type: CellType, var state: CellState, var position: Position = Position(0,0)) {
    var piece: Piece? = null
    fun getCellType(): CellType {
        return  this.type
    }

    fun getCellState(): CellState {
        return  this.state
    }

    fun isEmpty() : Boolean {
        return state == CellState.EMPTY && !isForbidden() && piece == null
    }

    fun isFilled() : Boolean {
        return state == CellState.FILLED && !isForbidden()
    }
    fun isForbidden(): Boolean {
        return  type == CellType.FORBIDDEN
    }

    fun isPossibleMovement(): Boolean {
        return  state == CellState.POSSIBLE_MOVE || state == CellState.POSSIBLE_KILL
    }

    fun isKillMovement(): Boolean {
        return state == CellState.POSSIBLE_KILL
    }

    fun setEmpty() {
        this.state = CellState.EMPTY
    }

    fun setFilled() {
        this.state = CellState.FILLED
    }


    fun placePiece(piece: Piece) {
        this.piece = piece
        piece.position = position
        this.setFilled()
    }

    fun removePiece() : Piece? {
        val removed = this.piece
        this.piece = null
        this.setEmpty()
        return removed
    }

    fun markAsPossible(){
        this.state = CellState.POSSIBLE_MOVE
    }

    fun markAsKillablePossible() {
        this.state = CellState.POSSIBLE_KILL
    }

    fun  changePosition(x: Int, y: Int){
        position.setPosition(x, y)
        if (piece != null){
            piece!!.position.setPosition(x,y)
        }
    }

    override fun toString(): String {
        var str = ""
        if(this.type == CellType.FORBIDDEN) {
            str += "X-"
        } else if(this.type == CellType.YELLOW) {
            str += "Y-"
        } else {
            str += "B-"
        }
        str += position

        if(this.piece != null) {
            str += "-"
            str += this.piece!!.team
        }
        return str
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cell) return false

        return type == other.type &&
                state == other.state &&
                piece == other.piece &&
                position == other.position

    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + (piece?.hashCode() ?: 0)
        result = 31 * result + position.hashCode()
        return result
    }

    companion object {
        const val forbiddenCode: Char = 'X'
        const val yellowEmptyCode: Char = 'Y'
        const val brownEmptyCode: Char = 'B'
        const val brownBlackPawn: Char = '#'
        const val brownWhitePawn: Char = '@'
        const val brownBlackDame: Char = '%'
        const val brownWhiteDame: Char = '&'
        fun fromCode(code: Char): Cell {
            when (code) {
                forbiddenCode -> {
                    return Cell(CellType.FORBIDDEN, CellState.EMPTY, Position(0,0))
                }
                yellowEmptyCode -> {
                    return Cell(CellType.YELLOW, CellState.EMPTY, Position(0,0))
                }
                brownEmptyCode -> {
                    return Cell(CellType.BROWN, CellState.EMPTY, Position(0,0))
                }
                brownWhitePawn -> {
                    val cell = Cell(CellType.BROWN, CellState.FILLED, Position(0,0))
                    cell.placePiece(Piece(PieceType.PAWN, Teams.WHITE))
                    return cell
                }
                brownBlackPawn -> {
                    val cell = Cell(CellType.BROWN, CellState.FILLED, Position(0,0))
                    cell.placePiece(Piece(PieceType.PAWN, Teams.BLACK))
                    return cell
                }
                brownWhiteDame -> {
                    val cell = Cell(CellType.BROWN, CellState.FILLED, Position(0,0))
                    cell.placePiece(Piece(PieceType.DAME, Teams.WHITE))
                    return cell
                }
                brownBlackDame -> {
                    val cell = Cell(CellType.BROWN, CellState.FILLED, Position(0,0))
                    cell.placePiece(Piece(PieceType.DAME, Teams.BLACK))
                    return cell
                }
                else -> {
                    return Cell(CellType.FORBIDDEN, CellState.EMPTY, Position(0,0))
                }
            }

        }
    }

}