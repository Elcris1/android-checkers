package com.example.checkers.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.checkers.components.GoBackAppBar
import com.example.checkers.datastore.DataStoreManager
import com.example.checkers.ui.theme.CheckersTheme
import kotlinx.coroutines.launch

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckersTheme {
                MyApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyApp() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    //SavedPreferences DataStore
    val dataStoreManager = DataStoreManager(context)
    val scope = rememberCoroutineScope()

    //DAta from store
    val aliasStore by dataStoreManager.alias.collectAsState(initial = "")
    val isWhiteStore by dataStoreManager.isWhiteTeam.collectAsState(initial = true)
    val isTimeEnabledStore by dataStoreManager.timeEnabled.collectAsState(initial = false)
    val minutesStore by dataStoreManager.minutes.collectAsState(initial = 0)
    val secs by dataStoreManager.seconds.collectAsState(initial = 0)

    //mutables to handle local edition
    var alias by rememberSaveable { mutableStateOf("") }
    var isWhite by rememberSaveable { mutableStateOf(true) }
    var isTimeEnabled by rememberSaveable { mutableStateOf(false) }
    var minutes by rememberSaveable { mutableStateOf("") }
    var seconds by rememberSaveable { mutableStateOf("") }

    //Set values
    LaunchedEffect(aliasStore) {
        alias = aliasStore
        isWhite = isWhiteStore
        isTimeEnabled = isTimeEnabledStore
        minutes = minutesStore.toString()
        seconds = secs.toString()
    }

    //var alias by rememberSaveable { mutableStateOf("") }


    Scaffold(
        topBar = { GoBackAppBar(stringResource(R.string.configuration_title)) },
        containerColor = Color(0xFF2E3B4E)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Alias
            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                label = { Text(stringResource(R.string.alias_placeholder), color = Color.White)  },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD0A43C),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.LightGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.LightGray
                )
            )

            // Equipo
            Text(
                text = stringResource(R.string.team),
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { isWhite = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWhite) Color(0xFFD0A43C) else Color.Gray
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.white_team))
                }

                Button(
                    onClick = { isWhite = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isWhite) Color(0xFFD0A43C) else Color.Gray
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.black_team))
                }
            }

            // Tiempo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.time_control),
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isTimeEnabled,
                    onCheckedChange = { isTimeEnabled = it }
                )
            }

            // Entrada de minutos y segundos
            if (isTimeEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = { minutes = it.filter { char -> char.isDigit() } },
                        label = { Text(stringResource(R.string.minutes), color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD0A43C),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.LightGray
                        )
                    )

                    Text(
                        ":",
                        fontSize = 22.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    OutlinedTextField(
                        value = seconds,
                        onValueChange = { seconds = it.filter { char -> char.isDigit() }.take(2) },
                        label = { Text(stringResource(R.string.seconds), color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD0A43C),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.LightGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.LightGray
                        )
                    )
                }
            }

            val toastText = stringResource(R.string.toast_save_configuration)
            Button(
                onClick = {
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                    scope.launch {
                        dataStoreManager.saveToDataStore(
                            alias,
                            isWhite,
                            isTimeEnabled,
                            minutes.toIntOrNull() ?: 0,
                            seconds.toIntOrNull() ?: 0
                        )
                    }
                },
                enabled = alias.isNotBlank() &&
                        (!isTimeEnabled || (minutes.isNotBlank() && seconds.isNotBlank())),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0A43C)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Text(text = stringResource(R.string.save_configuration))
            }
        }


    }
}
