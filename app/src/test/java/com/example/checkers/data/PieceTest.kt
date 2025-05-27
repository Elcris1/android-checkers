package com.example.checkers.data

import com.example.checkers.data.constants.PieceType
import com.example.checkers.data.constants.Teams
import org.junit.Test
import org.junit.Assert.*


class PieceTest {
    @Test
    fun `test isDame should return true for Dame pieces`() {
        // create a dame piece
        val piece = Piece(pieceType = PieceType.DAME, team = Teams.WHITE)

        // isDame should return true
        assertTrue(piece.isDame())
    }

    @Test
    fun `test isDame should return false for Pawn pieces`() {
        // create pawn piece
        val piece = Piece(pieceType = PieceType.PAWN, team = Teams.BLACK)

        // isdame should return false
        assertFalse(piece.isDame())
    }

    @Test
    fun `test changePosition should update position correctly`() {
        //Create a piece with an initial position
        val piece = Piece(pieceType = PieceType.PAWN, team = Teams.WHITE)
        piece.position = Position(0, 0) // Asignar una posición inicial

        // change position
        piece.changePosition(3, 4)

        // check position has been changed successfully
        assertEquals(3, piece.position.x)
        assertEquals(4, piece.position.y)
    }

    @Test
    fun `test convertToDame should change piece to Dame if not already Dame`() {
        // create pawn type piece
        val piece = Piece(pieceType = PieceType.PAWN, team = Teams.WHITE)

        // convert to dame
        piece.convertToDame()

        // check piece is successfully converted
        assertTrue(piece.isDame(),)
    }

    @Test
    fun `test convertToDame should do nothing if piece is already a Dame`() {
        // crate a dame type piece
        val piece = Piece(pieceType = PieceType.DAME, team = Teams.WHITE)

        // convert piece to dame again
        piece.convertToDame()

        //check piece still a dame
        assertTrue(piece.isDame())
    }

    @Test
    fun `test equals should return true for equal pieces`() {
        // create equal pieces
        val piece1 = Piece(pieceType = PieceType.PAWN, team = Teams.BLACK)
        piece1.position = Position(1, 1)

        val piece2 = Piece(pieceType = PieceType.PAWN, team = Teams.BLACK)
        piece2.position = Position(1, 1)

        // check equality
        assertEquals(piece1, piece2)
    }

    @Test
    fun `test equals should return false for different pieces`() {
        //create different pieces
        val piece1 = Piece(pieceType = PieceType.PAWN, team = Teams.BLACK)
        piece1.position = Position(1, 1)

        val piece2 = Piece(pieceType = PieceType.DAME, team = Teams.WHITE)
        piece2.position = Position(1, 1)

        // check pieces are different
        assertNotEquals(piece1, piece2)
    }

    @Test
    fun `test hashCode should be same for equal pieces`() {
        // Create two equal pieces
        val piece1 = Piece(pieceType = PieceType.PAWN, team = Teams.BLACK)
        piece1.position = Position(1, 1)

        val piece2 = Piece(pieceType = PieceType.PAWN, team = Teams.BLACK)
        piece2.position = Position(1, 1)

        // hashcode has to be the same for both
        assertEquals(piece1.hashCode(), piece2.hashCode())
    }

    @Test
    fun `test hashCode should be different for different pieces`() {
        // create different pieces
        val piece1 = Piece(pieceType = PieceType.PAWN, team = Teams.BLACK)
        piece1.position = Position(1, 1)

        val piece2 = Piece(pieceType = PieceType.DAME, team = Teams.WHITE)
        piece2.position = Position(2, 2)

        val piece3 = Piece(pieceType = PieceType.DAME, team = Teams.BLACK)
        piece3.position = Position(2, 2)

        // Check they have different hashCode
        assertNotEquals(piece1.hashCode(), piece2.hashCode())
        assertNotEquals(piece1.hashCode(), piece3.hashCode())
    }
}