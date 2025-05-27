package com.example.checkers.activities

import android.content.Intent
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkers.R
import com.example.checkers.ui.theme.CheckersTheme

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckersTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
private fun MyApp() {
    val context = LocalContext.current
    var alias by rememberSaveable { mutableStateOf("") }
    var whiteTeam by rememberSaveable { mutableStateOf(true) }
    var timeDeadline by rememberSaveable { mutableStateOf(false) }
    var minutes by rememberSaveable { mutableStateOf("") }
    var seconds by rememberSaveable { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E3B4E))
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.configuration_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        TextField(
            value = alias,
            onValueChange = { alias = it },
            label = { Text(stringResource(R.string.alias_placeholder)) },
            modifier = Modifier.fillMaxWidth(0.9f).padding(top = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text(stringResource(R.string.team), color = Color.White, fontSize = 16.sp)

        }

        Row(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Button(
                onClick = { whiteTeam = true },
                colors = ButtonDefaults.buttonColors(containerColor = if (whiteTeam) Color(0xFFD0A43C) else Color.Gray),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(stringResource(R.string.white_team))
            }
            Button(onClick = { whiteTeam = false }, colors = ButtonDefaults.buttonColors(containerColor = if (!whiteTeam) Color(0xFFD0A43C) else Color.Gray)) {
                Text(stringResource(R.string.black_team))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.time_control),
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            Switch(checked = timeDeadline, onCheckedChange = { timeDeadline = it })
        }

        if (timeDeadline) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextField(
                    value = minutes,
                    onValueChange = { minutes = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(R.string.minutes)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                Text(":", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

                TextField(
                    value = seconds,
                    onValueChange = { seconds = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(R.string.seconds)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Button(
            enabled = if(timeDeadline) alias.isNotBlank() && minutes.isNotBlank() && seconds.isNotBlank() else alias.isNotBlank(),
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("alias", alias)
                intent.putExtra("whiteTeam", whiteTeam )
                intent.putExtra("timeDeadline", timeDeadline)
                if (timeDeadline) {
                    intent.putExtra("minuteLimit", minutes.toInt())
                    intent.putExtra("secondLimit", seconds.toInt())
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(0.9f).padding(top = 16.dp)) {
            Text(stringResource(R.string.start_game))
        }
    }
}
