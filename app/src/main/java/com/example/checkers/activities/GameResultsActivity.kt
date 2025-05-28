package com.example.checkers.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.collection.emptyIntLongMap
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.example.checkers.activities.ui.theme.CheckersTheme
import com.example.checkers.viewmodels.ResultViewModel
import kotlinx.coroutines.flow.map

class GameResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val resultViewModel: ResultViewModel by viewModels()
        resultViewModel.gameResults.map {
            Log.d("logs", it.toString())
        }
        setContent {
            CheckersTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp(resultViewModel, innerPadding)
                }
            }
        }
    }
}

@Composable
private fun MyApp(resultsViewModel: ResultViewModel, padding: PaddingValues) {
    val results by resultsViewModel.gameResults.collectAsState(initial = emptyList())
    LazyColumn (modifier = Modifier.padding(padding)) {
        items(results) { result ->
            Text(result.alias + " " + result.result + " " + result.date)
        }
    }
}