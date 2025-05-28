package com.example.checkers.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.checkers.data.local.entity.GameResult
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: GameResult)

    @Query("SELECT * FROM game_results")
    fun getAll(): Flow<List<GameResult>>

    @Query("SELECT * FROM game_results where id = :id")
    suspend fun getById(id: Int): GameResult?

    @Query("DELETE FROM game_results")
    suspend fun clear()
}