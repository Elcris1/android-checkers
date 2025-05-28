package com.example.checkers.data.local.repository

import androidx.annotation.WorkerThread
import com.example.checkers.data.local.dao.GameResultDAO
import com.example.checkers.data.local.entity.GameResult
import kotlinx.coroutines.flow.Flow

class GameResultRepository(private val dao: GameResultDAO) {
    val results: Flow<List<GameResult>> = dao.getAll()

    @WorkerThread
    suspend fun insert(result: GameResult) {
        dao.insert(result)
    }

    suspend fun getById(id: Int): GameResult? {
        return dao.getById(id)
    }

    suspend fun clear() {
        dao.clear()
    }

}