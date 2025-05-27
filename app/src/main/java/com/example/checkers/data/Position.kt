package com.example.checkers.data

class Position (var x: Int, var y: Int) {
    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Position) return false

        return x == other.x &&
                y == other.y
    }

    override fun hashCode(): Int {
        return x.hashCode() + y.hashCode()
    }
}