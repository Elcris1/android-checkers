package com.example.checkers.data

import org.junit.Test

import org.junit.Assert.*

class PositionTest {
    @Test
    fun `test position equality`() {
        // Create two positions with same values
        val pos1 = Position(1, 2)
        val pos2 = Position(1, 2)

        // Check equality
        assertEquals(pos1, pos2)
    }

    @Test
    fun `test position inequality`() {
        // Create two positions with different values
        val pos1 = Position(1, 2)
        val pos2 = Position(2, 3)

        // Check inequality
        assertNotEquals(pos1, pos2)
    }

    @Test
    fun `test position hashCode`() {
        // Create two positions with same values
        val pos1 = Position(1, 2)
        val pos2 = Position(1, 2)

        // Check if they have same hashCode
        assertEquals(pos1.hashCode(), pos2.hashCode())
    }

    @Test
    fun `test different positions have different hashCodes`() {
        // Create two positions with different values
        val pos1 = Position(1, 2)
        val pos2 = Position(2, 3)

        // Check different hashCode
        assertNotEquals(pos1.hashCode(), pos2.hashCode())
    }

    @Test
    fun `test position setPosition function`() {
        // create initial position
        val pos = Position(0, 0)

        // Change that position
        pos.setPosition(3, 4)

        // Check that values have changed
        assertEquals(3, pos.x)
        assertEquals(4, pos.y)
    }
}