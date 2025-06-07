package com.example.checkers.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "game_results")
data class GameResult (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val alias: String,
    val userTeam: String,
    val winnerTeam: String,
    val result: String,
    val userPieces: Int,
    val cpuPieces: Int,
    val totalMovements: Int,
    val timeDeadLine: Boolean,
    val leftOverTime: String,
    val date: String
) : Serializable

