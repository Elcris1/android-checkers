package com.example.checkers.data

import androidx.compose.ui.res.stringResource
import com.example.checkers.R
import com.example.checkers.data.local.entity.GameResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameResultBuilder {
    var alias: String = ""
    var userTeam: String = ""
    var result: String = ""
    var userPieces: Int = 0
    var cpuPieces: Int = 0
    var totalMovements: Int = 0
    var timeDeadLine: Boolean = false
    var leftOverTime: String = ""

    fun build(): GameResult {
        return GameResult(
            alias = alias,
            userTeam =  userTeam,
            result = result,
            userPieces = userPieces,
            cpuPieces = cpuPieces,
            totalMovements = totalMovements,
            timeDeadLine = timeDeadLine,
            leftOverTime = leftOverTime,
            date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        )
    }
}