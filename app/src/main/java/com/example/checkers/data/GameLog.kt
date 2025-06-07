package com.example.checkers.data

import android.annotation.SuppressLint
import android.content.Context
import com.example.checkers.R

class GameLog(val key: String, vararg val args: String) {

    companion object {
        const val MOVEMENT = "movement" //KEY FOR movement log
        const val REMAINING_TIME = "remaining_time" //KEY for remaining time
        const val NUMBER_OF_BLACK = "black_pieces"
        const val NUMBER_OF_WHITE = "white_pieces"
        const val KILL_MOVEMENT = "kill_movement"
        const val TIME_DEADLINE_REACHED = "time_deadline_completed"
        const val VICTORY = "victory"
        const val TIME_LIMIT = "time_limit"
        const val TURN = "turn"
        const val ASCENSION = "ascension"

        @SuppressLint("StringFormatInvalid", "StringFormatMatches")
        fun getString(context: Context, gameLog: GameLog) : String {
            return when (gameLog.key) {
                MOVEMENT -> context.getString(R.string.logs_movement, *gameLog.args)
                REMAINING_TIME -> context.getString(R.string.logs_time_left, *gameLog.args)
                NUMBER_OF_BLACK -> context.getString(R.string.logs_black_pieces, *gameLog.args)
                NUMBER_OF_WHITE -> context.getString(R.string.logs_white_pieces, *gameLog.args)
                KILL_MOVEMENT -> context.getString(R.string.logs_kill, *gameLog.args)
                TIME_DEADLINE_REACHED -> context.getString(R.string.logs_time_limit, *gameLog.args)
                VICTORY -> context.getString(R.string.logs_win, *gameLog.args)
                TIME_LIMIT -> context.getString(R.string.logs_time_limit)
                TURN -> context.getString(R.string.logs_turn, *gameLog.args)
                else -> ""
            }
        }
    }
}