package com.example.checkers.data

import com.example.checkers.data.constants.PieceType
import com.example.checkers.data.constants.Teams
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class BoardTest {

    val startingStringBoard = """
    XXXXXXXXXX
    X#Y#Y#Y#YX
    XY#Y#Y#Y#X
    X#Y#Y#Y#YX
    XYBYBYBYBX
    XBYBYBYBYX
    XY@Y@Y@Y@X
    X@Y@Y@Y@YX
    XY@Y@Y@Y@X
    XXXXXXXXXX
""".trimIndent().replace("\n", "")

    val emptyBoard = """
    XXXXXXXXXX
    XBYBYBYBYX
    XYBYBYBYBX
    XBYBYBYBYX
    XYBYBYBYBX
    XBYBYBYBYX
    XYBYBYBYBX
    XBYBYBYBYX
    XYBYBYBYBX
    XXXXXXXXXX
""".trimIndent().replace("\n", "")

    private fun editStringBoard(x: Int, y: Int, string: String, char: Char): String{
        val index = y * 10 + x
        val stringBuilder = StringBuilder(string)
        stringBuilder.setCharAt(index, char)
        return stringBuilder.toString()
    }

    private lateinit var board: Board
    @Before
    fun setUp() {
        board = Board()
        board.createStartingBoard()
    }

    @Test
    fun test_board_equality() {
        assertEquals(board, board)
        val board2 = Board()
        board2.createStartingBoard()
        assertEquals(board, board2)
    }

    @Test
    fun from_string() {
        val board2 = Board()
        board2.createBoardFromString(this.startingStringBoard)
        assertEquals(board, board2)
    }

    @Test
    fun getCell(){
        val cell = board.getCell(1,1)
        val equalCell = Cell.fromCode('#')
        equalCell.changePosition(1,1)

        assertEquals(cell, equalCell)
    }

    @Test
    fun diagonal_right() {

        val whiteCell = board.getDiagonalRight(4,4)
        assertEquals(whiteCell, board.getCell(5, 3))

        val blackCell = board.getDiagonalRight(4,4, Teams.BLACK)
        assertEquals(blackCell, board.getCell(5, 5))
    }

    @Test
    fun diagonal_left() {
        val whiteCell = board.getDiagonalLeft(4,4)
        assertEquals(whiteCell, board.getCell(3, 3))

        val blackCell = board.getDiagonalLeft(4,4, Teams.BLACK)
        assertEquals(blackCell, board.getCell(3, 5))
    }

    @Test
    fun far_diagonal_right() {
        val whiteCell = board.getFarDiagonalRight(4,4)
        assertEquals(whiteCell, board.getCell(6, 2))

        val blackCell = board.getFarDiagonalRight(4,4, Teams.BLACK)
        assertEquals(blackCell, board.getCell(6, 6))
    }
    @Test
    fun far_diagonal_left() {
        val whiteCell = board.getFarDiagonalLeft(4,4)
        assertEquals(whiteCell, board.getCell(2, 2))

        val blackCell = board.getFarDiagonalLeft(4,4, Teams.BLACK)
        assertEquals(blackCell, board.getCell(2, 6))
    }

    @Test
    fun isDiagonalRightKillable_whitePawn() {
        val x = 4
        val y = 4
        var boardString = editStringBoard(x, y, emptyBoard, Cell.brownWhitePawn)
        boardString = editStringBoard(x+1,y-1, boardString, Cell.brownBlackPawn)
        board.createBoardFromString(boardString)

        //simple kill
        assertTrue(board.isDiagonalRightKillable(x,y, board.getCell(x,y).piece!!))

        //Cannot kill because landing position is filled
        board.placePiece(x+2, y-2, Piece(PieceType.PAWN, Teams.BLACK))
        assertFalse(board.isDiagonalRightKillable(x,y, board.getCell(x,y).piece!!))
        board.removePiece(x+2, y-2)

        //Cannot kill because piece is from the same team
        board.getCell(x+1,y-1).piece = Piece(PieceType.PAWN, Teams.WHITE, Position(x+1, y-1))
        assertFalse(board.isDiagonalRightKillable(x,y, board.getCell(x,y).piece!!))

        //Cannot kill because theres nothing to be killed
        board.getCell(x+1,y-1).removePiece()
        assertFalse(board.isDiagonalRightKillable(x,y, board.getCell(x,y).piece!!))

        //Cannot kill because there is no space to kill
        val newX = 7
        val newY = 5
        val piece = Piece(PieceType.PAWN, Teams.WHITE)
        board.placePiece(newX, newY, piece)
        board.placePiece(newX + 1,newY-1, Piece(PieceType.PAWN, Teams.BLACK))
        assertFalse(board.isDiagonalRightKillable(newX,newY, piece))
    }

    @Test
    fun isDiagonalRightKillable_blackPawn() {

        val x = 4
        val y = 4
        var boardString = editStringBoard(x, y, emptyBoard, Cell.brownBlackPawn)
        boardString = editStringBoard(x+1,y+1, boardString, Cell.brownWhitePawn)
        board.createBoardFromString(boardString)


        //simple kill
        assertTrue(board.isDiagonalRightKillable(x,y, board.getCell(x,y).piece!!))

        //Cannot kill because landing position is filled
        board.placePiece(x+2,y+2, Piece(PieceType.PAWN, Teams.WHITE))
        assertFalse(board.isDiagonalRightKillable(x,y, board.getCell(x,y).piece!!))

        //Cannot kill because piece is from the same team
        board.removePiece(x+1, y-1)
        board.placePiece(x+1,y-1, Piece(PieceType.PAWN, Teams.BLACK, Position(x+1, y-1)))
        assertFalse(board.isDiagonalRightKillable(x,y, board.getCell(x,y).piece!!))

        //Cannot kill because theres nothing to be killed
        board.getCell(x+1,y-1).removePiece()
        assertFalse(board.isDiagonalRightKillable(x,y, board.getCell(x,y).piece!!))

        //Cannot kill because there is no space to kill
        val newX = 7
        val newY = 5
        val piece = Piece(PieceType.PAWN, Teams.BLACK)
        board.placePiece(newX, newY, piece)
        board.placePiece(newX + 1,newY-1, Piece(PieceType.PAWN, Teams.WHITE))
        assertFalse(board.isDiagonalRightKillable(newX,newY, piece))

    }

    @Test
    fun isDiagonalLeftKillable_whitePawn(){
    }

    @Test
    fun isDiagonalLeftKillable_blackPawn() {
    }

    @Test
    fun showPossibleMoves_PAWN_DefaultExecution() {
        //Vars for testability
        //White test
        var x = 2
        var y = 6
        var team = Teams.WHITE

        //Test basic movement
        board.showPossibleMovement(x, y, team)
        assertTrue(board.getDiagonalLeft(x, y, team).isPossibleMovement())
        assertTrue(board.getDiagonalRight(x, y, team).isPossibleMovement())

        //Black Test
        x = 3
        y = 3
        team = Teams.BLACK

        //test move on the leftest position should only mark one cell
        board.showPossibleMovement(x,y,team)
        assertTrue(board.getDiagonalLeft(x, y, team).isPossibleMovement())
        assertTrue(board.getDiagonalRight(x, y, team).isPossibleMovement())
    }

    @Test
    fun showPossibleMoves_PAWN_furthest_cell_should_mark_one() {

        //Vars for testability
        //White test
        var x = 8
        var y = 6
        var team = Teams.WHITE
        board.showPossibleMovement(x,y,team)
        assertTrue(board.getDiagonalLeft(x,y,team).isPossibleMovement())
        assertFalse(board.getDiagonalRight(x,y,team).isPossibleMovement())

        //Black Test
        x = 1
        y = 3
        team = Teams.BLACK

        //test move on the leftest position should only mark one cell
        board.showPossibleMovement(x,y,team)
        assertFalse(board.getDiagonalLeft(x, y, team).isPossibleMovement())
        assertTrue(board.getDiagonalRight(x, y, team).isPossibleMovement())
    }

    @Test
    fun showPossibleMoves_DAME_defaultExecution() {
        val x = 4
        val y = 4
        val boardString = editStringBoard(x, y, emptyBoard, Cell.brownWhiteDame)
        board.createBoardFromString(boardString)

        board.showPossibleMovement(x, y, Teams.WHITE)

        assertTrue(board.getDiagonalLeft(x, y, Teams.WHITE).isPossibleMovement())
        assertTrue(board.getDiagonalRight(x, y, Teams.WHITE).isPossibleMovement())
        assertTrue(board.getDiagonalLeft(x, y, Teams.BLACK).isPossibleMovement())
        assertTrue(board.getDiagonalRight(x, y, Teams.BLACK).isPossibleMovement())

    }




}