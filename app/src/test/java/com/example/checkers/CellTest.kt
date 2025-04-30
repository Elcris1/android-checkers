package com.example.checkers

import org.junit.Test

import org.junit.Assert.*

class CellTest {
    @Test

    fun isEmptyShouldReturnTrueForEmptyCells() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)

        // check isempty returns true
        assertTrue(cell.isEmpty())
    }

    @Test
    fun isEmpty_shouldReturnFalseForFilledCells() {
        val cell = Cell(CellType.BROWN, CellState.FILLED)

        // Check isEmpty returns false
        assertFalse(cell.isEmpty())
    }

    @Test
    fun isFilled_shouldReturnTrueForFilledCells() {
        val cell = Cell(CellType.BROWN, CellState.FILLED)

        // isFilled should return true
        assertTrue(cell.isFilled())
    }

    @Test
    fun isFilled_shouldReturnFalseForEmptyCells() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)

        // isFilled should return false
        assertFalse(cell.isFilled())
    }

    @Test
    fun isForbidden_shouldReturnTrueForForbiddenCells() {
        val cell = Cell(CellType.FORBIDDEN, CellState.EMPTY)

        // check forbidden
        assertTrue(cell.isForbidden())
    }

    @Test
    fun isForbidden_shouldReturnFalseForNonForbiddenCells() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)

        // isForbidden returns false
        assertFalse(cell.isForbidden())
    }

    @Test
    fun placePiece_shouldSetPieceAndFillCell() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)
        val piece = Piece(PieceType.PAWN, Teams.WHITE)

        // place the piece
        cell.placePiece(piece)

        // verify that the cell is filled and the piece is assigned to the cell
        assertTrue(cell.isFilled())
        assertEquals(piece, cell.piece)
    }

    @Test
    fun removePiece_shouldEmptyCellAndRemovePiece() {
        val cell = Cell(CellType.BROWN, CellState.FILLED)
        val piece = Piece(PieceType.PAWN, Teams.WHITE)

        // Place the piece
        cell.placePiece(piece)

        // Remove the piece
        cell.removePiece()

        // verify that the cell is empty and the piece is unassigned from the cell
        assertTrue(cell.isEmpty())
        assertNull(cell.piece)
    }

    @Test
    fun markAsPossible_shouldChangeStateToPossibleMove() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)

        // mark the cell as a possible movement
        cell.markAsPossible()

        // verify that the state has been changed
        assertEquals(CellState.POSSIBLE_MOVE, cell.state)
    }

    @Test
    fun equals_shouldReturnTrueForEqualCells() {
        val cell1 = Cell(CellType.BROWN, CellState.EMPTY)
        cell1.position = Position(0, 0)

        val cell2 = Cell(CellType.BROWN, CellState.EMPTY)
        cell2.position = Position(0, 0)

        // Verify equality
        assertEquals(cell1, cell2)
    }

    @Test
    fun equals_shouldReturnFalseForDifferentCells() {
        val cell1 = Cell(CellType.BROWN, CellState.EMPTY)
        cell1.position = Position(0, 0)

        val cell2 = Cell(CellType.YELLOW, CellState.EMPTY)
        cell2.position = Position(0, 0)

        // verify inequality
        assertNotEquals(cell1, cell2)
    }
    @Test
    fun testHashCodeShouldBeDifferentForDifferentCells() {
        val cell1 = Cell(CellType.BROWN, CellState.EMPTY)
        cell1.position = Position(0, 0)

        val cell2 = Cell(CellType.BROWN, CellState.EMPTY)
        cell2.position = Position(0, 0)
        cell2.placePiece(Piece(PieceType.PAWN, Teams.BLACK))

        // Verificar que las celdas distintas tengan hashCode diferente
        assertNotEquals(cell1.hashCode(), cell2.hashCode())
    }

    @Test
    fun testFromCodeForbiddenCell() {
        val cell = Cell.fromCode('X')
        assertEquals(CellType.FORBIDDEN, cell.type)
        assertEquals(CellState.EMPTY, cell.state)
        assertNull(cell.piece)
    }

    @Test
    fun testFromCodeYellowEmptyCell() {
        val cell = Cell.fromCode('Y')
        assertEquals(CellType.YELLOW, cell.type)
        assertEquals(CellState.EMPTY, cell.state)
        assertNull(cell.piece)
    }

    @Test
    fun testFromCodeBrownEmptyCell() {
        val cell = Cell.fromCode('B')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.EMPTY, cell.state)
        assertNull(cell.piece)
    }

    @Test
    fun testFromCodeBrownFilledBlackPawn() {
        val cell = Cell.fromCode('#')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.FILLED, cell.state)
        assertNotNull(cell.piece)
        assertEquals(PieceType.PAWN, cell.piece!!.pieceType)
        assertEquals(Teams.BLACK, cell.piece!!.team)
    }

    @Test
    fun testFromCodeBrownFilledWhitePawn() {
        val cell = Cell.fromCode('@')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.FILLED, cell.state)
        assertNotNull(cell.piece)
        assertEquals(PieceType.PAWN, cell.piece!!.pieceType)
        assertEquals(Teams.WHITE, cell.piece!!.team)
    }

    @Test
    fun testFromCodeBrownFilledBlackDame() {
        val cell = Cell.fromCode('%')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.FILLED, cell.state)
        assertNotNull(cell.piece)
        assertEquals(PieceType.DAME, cell.piece!!.pieceType)
        assertEquals(Teams.BLACK, cell.piece!!.team)
    }

    @Test
    fun testFromCodeBrownFilledWhiteDame() {
        val cell = Cell.fromCode('&')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.FILLED, cell.state)
        assertNotNull(cell.piece)
        assertEquals(PieceType.DAME, cell.piece!!.pieceType)
        assertEquals(Teams.WHITE, cell.piece!!.team)
    }

    @Test
    fun testFromCodeDefault() {
        val cell = Cell.fromCode('?')
        assertEquals(CellType.FORBIDDEN, cell.type)
        assertEquals(CellState.EMPTY, cell.state)
        assertNull(cell.piece)
    }



}