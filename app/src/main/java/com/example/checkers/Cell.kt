package com.example.checkers

import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.awaitCancellation

class Cell(var type: CellType, var state: CellState, ) {
    var piece: Piece? = null
    lateinit var position: Position
    fun getCellType(): CellType {
        return  this.type
    }

    fun getCellState(): CellState {
        return  this.state
    }

    fun isEmpty() : Boolean {
        return state == CellState.EMPTY && !isForbidden()
    }

    fun isFilled() : Boolean {
        return state == CellState.FILLED && !isForbidden()
    }
    fun isForbidden(): Boolean {
        return  type == CellType.FORBIDDEN
    }

    fun setEmpty() {
        if (this.state != CellState.EMPTY) this.state = CellState.EMPTY
    }

    fun setFilled() {
        if(this.state != CellState.FILLED) this.state = CellState.FILLED
    }


    fun placePiece(piece: Piece) {
        this.piece = piece
        this.setFilled()
    }

    fun removePiece() {
        this.piece = null
        this.setEmpty()
    }

    fun markAsPossible(){
        this.state = CellState.POSSIBLE_MOVE
    }

    override fun toString(): String {
        var str = ""
        if(this.type == CellType.FORBIDDEN) {
            str += "X"
        } else if(this.type == CellType.YELLOW) {
            str += "Y"
        } else {
            str += "B"
        }
        if(this.piece != null) {
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
        if (::position.isInitialized) {
            result = 31 * result + position.hashCode()
        } else {
            result = 31 * result + 0
        }
        return result
    }

    companion object {
        private const val forbiddenCode: Char = 'X'
        private const val yellowEmptyCode: Char = 'Y'
        private const val brownEmptyCode: Char = 'B'
        private const val brownBlackPawn: Char = '#'
        private const val brownWhitePawn: Char = '@'
        private const val brownBlackDame: Char = '%'
        private const val brownWhiteDame: Char = '&'
        fun fromCode(code: Char): Cell {
            when (code) {
                forbiddenCode -> {
                    return Cell(CellType.FORBIDDEN, CellState.EMPTY)
                }
                yellowEmptyCode -> {
                    return Cell(CellType.YELLOW, CellState.EMPTY)
                }
                brownEmptyCode -> {
                    return Cell(CellType.BROWN, CellState.EMPTY)
                }
                brownWhitePawn -> {
                    val cell = Cell(CellType.BROWN, CellState.FILLED)
                    cell.placePiece(Piece(PieceType.PAWN, Teams.WHITE))
                    return cell
                }
                brownBlackPawn -> {
                    val cell = Cell(CellType.BROWN, CellState.FILLED)
                    cell.placePiece(Piece(PieceType.PAWN, Teams.BLACK))
                    return cell
                }
                brownWhiteDame -> {
                    val cell = Cell(CellType.BROWN, CellState.FILLED)
                    cell.placePiece(Piece(PieceType.DAME, Teams.WHITE))
                    return cell
                }
                brownBlackDame -> {
                    val cell = Cell(CellType.BROWN, CellState.FILLED)
                    cell.placePiece(Piece(PieceType.DAME, Teams.BLACK))
                    return cell
                }
                else -> {
                    return Cell(CellType.FORBIDDEN, CellState.EMPTY)
                }
            }

        }
    }

}