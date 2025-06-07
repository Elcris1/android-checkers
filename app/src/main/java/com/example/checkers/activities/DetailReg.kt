package com.example.checkers.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import com.example.checkers.R
import com.example.checkers.activities.ui.theme.CheckersTheme
import com.example.checkers.components.GoBackAppBar
import com.example.checkers.data.local.entity.GameResult
import com.example.checkers.viewmodels.ResultViewModel

class DetailReg : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val result  = intent.getSerializableExtra("result") as GameResult
        setContent {

            CheckersTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { GoBackAppBar(stringResource(R.string.game_details)) },
                    containerColor = Color(0xFF2E3B4E)
                ) { innerPadding ->
                    MyApp(result, Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    private fun MyApp(result: GameResult, modifier: Modifier = Modifier){

        Text(result.date, modifier)

    }
}



