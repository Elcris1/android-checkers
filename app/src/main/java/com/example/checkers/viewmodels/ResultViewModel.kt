package com.example.checkers.viewmodels

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.checkers.data.local.database.CheckersDatabase
import com.example.checkers.data.local.entity.GameResult
import com.example.checkers.data.local.repository.GameResultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResultViewModel(application: Application): AndroidViewModel(application) {
    private val dao = CheckersDatabase.getInstance(application).gameResultsDao()
    private val repository = GameResultRepository(dao)
    val gameResults: Flow<List<GameResult>> = repository.results


    fun addGameResult(result: GameResult) {
        viewModelScope.launch {
            repository.insert(result)
        }
    }

    fun getGameResultById(id: Int): LiveData<GameResult?> {
        val result = MutableLiveData<GameResult?>()
        viewModelScope.launch {
            result.postValue(repository.getById(id))
        }
        return result
    }

    fun clearResults(){
        viewModelScope.launch {
            repository.clear()
        }
    }
}