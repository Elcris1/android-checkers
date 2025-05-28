package com.example.checkers.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.checkers.R
import com.example.checkers.activities.ConfigurationActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralAppBar(
    title: String
) {
    val context = LocalContext.current

    TopAppBar(
        title = { Text(title, color = Color.White) },
        actions = {
            IconButton(
                onClick = {
                    openSettingsScreen(context)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.config_buton),
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1E2A38),
            titleContentColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoBackAppBar(
    title: String
) {
    val context = LocalContext.current

    TopAppBar(
        title = { Text(text = title, color = Color.White) },
        navigationIcon = {
                IconButton(
                    onClick = {
                        (context as? Activity)?.finish()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back),
                        tint = Color.White
                    )
                }
            }
        ,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1E2A38),
            titleContentColor = Color.White
        )
    )
}

private fun openSettingsScreen(context: Context) {
    context.startActivity(Intent(context, ConfigurationActivity::class.java))
}