package com.example.checkers

class Piece(var pieceType: PieceType = PieceType.PAWN, val team: Teams, var position: Position = Position(0,0)) {
    fun isDame(): Boolean {
        return this.pieceType == PieceType.DAME
    }

    fun changePosition(x: Int, y: Int) {
        this.position.setPosition(x, y)
    }
    fun convertToDame() {
        if(!this.isDame()) this.pieceType = PieceType.DAME
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Piece) return false

        return pieceType == other.pieceType &&
                team == other.team &&
                position == other.position
    }

    override fun hashCode(): Int {

        return pieceType.hashCode() + team.hashCode() + position.hashCode()
    }

    override fun toString(): String {
        return "$team$pieceType$position"
    }

}