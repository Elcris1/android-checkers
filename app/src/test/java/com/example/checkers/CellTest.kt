package com.example.checkers

import org.junit.Test

import org.junit.Assert.*

class CellTest {
    @Test
    fun `test isEmpty should return true for empty cells`() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)

        // check isempty returns true
        assertTrue(cell.isEmpty())
    }

    @Test
    fun `test isEmpty should return false for filled cells`() {
        val cell = Cell(CellType.BROWN, CellState.FILLED)

        // Check isEmpty returns false
        assertFalse(cell.isEmpty())
    }

    @Test
    fun `test isFilled should return true for filled cells`() {
        val cell = Cell(CellType.BROWN, CellState.FILLED)

        // isFilled should return true
        assertTrue(cell.isFilled())
    }

    @Test
    fun `test isFilled should return false for empty cells`() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)

        // isFilled should return false
        assertFalse(cell.isFilled())
    }

    @Test
    fun `test isForbidden should return true for forbidden cells`() {
        val cell = Cell(CellType.FORBIDDEN, CellState.EMPTY)

        // check forbidden
        assertTrue(cell.isForbidden())
    }

    @Test
    fun `test isForbidden should return false for non-forbidden cells`() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)

        // isForbidden returns false
        assertFalse(cell.isForbidden())
    }

    @Test
    fun `test placePiece should set the piece and fill the cell`() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)
        val piece = Piece(PieceType.PAWN, Teams.WHITE)

        // place the piece
        cell.placePiece(piece)

        // verify that the cell is filled and the piece is assigned to the cell
        assertTrue(cell.isFilled())
        assertEquals(piece, cell.piece)
    }

    @Test
    fun `test removePiece should empty the cell and remove the piece`() {
        val cell = Cell(CellType.BROWN, CellState.FILLED)
        val piece = Piece(PieceType.PAWN, Teams.WHITE)

        // Place the piece
        cell.placePiece(piece)

        // Remove the piece
        cell.removePiece()

        // verify that the cell is empty and the piece is unassigned form the cell
        assertTrue(cell.isEmpty())
        assertNull(cell.piece)
    }

    @Test
    fun `test markAsPossible should change the cell state to POSSIBLE_MOVE`() {
        val cell = Cell(CellType.BROWN, CellState.EMPTY)

        // mark the cell as a possible movement
        cell.markAsPossible()

        // verify that the state has been changed
        assertEquals(CellState.POSSIBLE_MOVE, cell.state)
    }


    @Test
    fun `test equals should return true for equal cells`() {
        val cell1 = Cell(CellType.BROWN, CellState.EMPTY)
        cell1.position = Position(0, 0)

        val cell2 = Cell(CellType.BROWN, CellState.EMPTY)
        cell2.position = Position(0, 0)

        // Verify equality
        assertEquals(cell1, cell2)
    }

    @Test
    fun `test equals should return false for different cells`() {
        val cell1 = Cell(CellType.BROWN, CellState.EMPTY)
        cell1.position = Position(0, 0)

        val cell2 = Cell(CellType.YELLOW, CellState.EMPTY)
        cell2.position = Position(0, 0)

        // verify inequality
        assertNotEquals(cell1, cell2)
    }

    @Test
    fun `test hashCode should be same for equal cells`() {
        val cell1 = Cell(CellType.BROWN, CellState.EMPTY)
        cell1.position = Position(0, 0)

        val cell2 = Cell(CellType.BROWN, CellState.EMPTY)
        cell2.position = Position(0, 0)

        // verify that same cells have the same hashCode
        assertEquals(cell1.hashCode(), cell2.hashCode())

        val piece = Piece(pieceType = PieceType.DAME, team = Teams.BLACK)
        cell1.placePiece(piece)
        cell2.placePiece(piece)
        assertEquals(cell1.hashCode(), cell2.hashCode())
    }

    @Test
    fun `test hashCode should be different for different cells`() {
        val cell1 = Cell(CellType.BROWN, CellState.EMPTY)
        cell1.position = Position(0, 0)

        val cell2 = Cell(CellType.BROWN, CellState.EMPTY)
        cell2.position = Position(0, 0)
        cell2.placePiece(Piece(PieceType.PAWN, Teams.BLACK))

        // verify that different cells have different hashCode
        assertNotEquals(cell1.hashCode(), cell2.hashCode())
    }



    @Test

    fun `test fromCode forbiddenCell`() {
        val cell = Cell.fromCode('X')
        assertEquals(CellType.FORBIDDEN, cell.type)
        assertEquals(CellState.EMPTY, cell.state)
        assertNull(cell.piece)
    }

    @Test
    fun `test fromCode Yellow empty Cell`() {
        val cell = Cell.fromCode('Y')
        assertEquals(CellType.YELLOW, cell.type)
        assertEquals(CellState.EMPTY, cell.state)
        assertNull(cell.piece)
    }

    @Test
    fun `test fromCode brown empty cell`() {
        val cell = Cell.fromCode('B')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.EMPTY, cell.state)
        assertNull(cell.piece)
    }

    @Test
    fun `test fromCode brown filled black pawn`() {
        val cell = Cell.fromCode('#')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.FILLED, cell.state)
        assertNotNull(cell.piece)
        assertEquals(PieceType.PAWN, cell.piece!!.pieceType)
        assertEquals(Teams.BLACK, cell.piece!!.team)
    }

    @Test
    fun `test fromCode brown filled white pawn`() {
        val cell = Cell.fromCode('@')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.FILLED, cell.state)
        assertNotNull(cell.piece)
        assertEquals(PieceType.PAWN, cell.piece!!.pieceType)
        assertEquals(Teams.WHITE, cell.piece!!.team)
    }

    @Test
    fun `test fromCode brown filled black dame`() {
        val cell = Cell.fromCode('%')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.FILLED, cell.state)
        assertNotNull(cell.piece)
        assertEquals(PieceType.DAME, cell.piece!!.pieceType)
        assertEquals(Teams.BLACK, cell.piece!!.team)
    }

    @Test
    fun `test fromCode brown filled white dame`() {
        val cell = Cell.fromCode('&')
        assertEquals(CellType.BROWN, cell.type)
        assertEquals(CellState.FILLED, cell.state)
        assertNotNull(cell.piece)
        assertEquals(PieceType.DAME, cell.piece!!.pieceType)
        assertEquals(Teams.WHITE, cell.piece!!.team)
    }

    @Test
    fun `test fromCode default`() {
        val cell = Cell.fromCode('?')
        assertEquals(CellType.FORBIDDEN, cell.type)
        assertEquals(CellState.EMPTY, cell.state)
        assertNull(cell.piece)
    }
}