package com.example.checkers.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.checkers.data.local.dao.GameResultDAO
import com.example.checkers.data.local.entity.GameResult

@Database(entities = [GameResult::class], version = 2)
abstract class CheckersDatabase: RoomDatabase() {
    abstract fun gameResultsDao() : GameResultDAO
    companion object {
        @Volatile private var INSTANCE: CheckersDatabase? = null

        fun getInstance(context: Context): CheckersDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CheckersDatabase::class.java,
                    "checkers_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}