package com.example.checkers.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.checkers.data.local.entity.GameResult
import com.example.checkers.data.local.repository.GameResultRepository
import kotlinx.coroutines.launch

class ResultViewModel(private val repository: GameResultRepository): ViewModel() {
    val gameResults: LiveData<List<GameResult>> = repository.results.asLiveData()

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