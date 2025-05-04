package com.example.checkers

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.finishAffinity
import com.example.checkers.ui.theme.CheckersTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val alias = intent.getStringExtra("alias") ?: "Player"
        val result = intent.getStringExtra("result") ?: "WHITE"
        val userTeam = intent.getStringExtra("userTeam") ?: "WHITE"
        val userPieces = intent.getIntExtra("numberPiecesUser", 0)
        val cpuPieces = intent.getIntExtra("numberPiecesCPU", 0)
        val movements = intent.getIntExtra("movements", 0)
        val time = intent.getStringExtra("time") ?: ""

        val finalizationDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        setContent {
            CheckersTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
                    MyApp(alias, result, userTeam, userPieces, cpuPieces, movements, time, finalizationDate)
                }
            }
        }
    }
}

@Composable
private fun MyApp(
    alias: String,
    result: String,
    userTeam: String,
    userPieces: Int,
    cpuPieces: Int,
    movements: Int,
    time: String,
    finalizationDate: String
) {
    //TODO: make horizontal
    var email by rememberSaveable { mutableStateOf("ecp16@alumnes.udl.cat") }
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E3B4E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.game_results),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp)
        )

        ResultItem(stringResource(R.string.alias), alias)
        ResultItem(stringResource(R.string.result), result)
        ResultItem(stringResource(R.string.user_team), userTeam)
        ResultItem(stringResource(R.string.user_pieces), "$userPieces")
        ResultItem(stringResource(R.string.cpu_pieces), "$cpuPieces")
        ResultItem(stringResource(R.string.movements), "$movements")
        ResultItem(stringResource(R.string.time), if(time == "") stringResource(R.string.no_time) else time)
        ResultItem(stringResource(R.string.time_finalization), finalizationDate)

        var text = stringResource(R.string.result_msg, finalizationDate, alias, userTeam, result, movements, userPieces, cpuPieces)
        if (time != "") {
            text += stringResource(R.string.time_involved, time)
        }

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_placeholder)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(0.95f)
        )

        //TODO: THIS LOGIC
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(0.95f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0A43C))
        ) {
            Text(stringResource(R.string.send), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        //TODO: this intent
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(0.95f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0A43C))
        ) {
            Text(stringResource(R.string.new_game), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Button(
            onClick = {
                (context as? Activity)?.finishAffinity()
            },
            modifier = Modifier.fillMaxWidth(0.95f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0A43C))
        ) {
            Text(stringResource(R.string.exit), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun ResultItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(value, fontSize = 18.sp, color = Color.White)
    }
}