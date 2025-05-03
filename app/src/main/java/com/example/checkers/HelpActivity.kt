package com.example.checkers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkers.ui.theme.CheckersTheme

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckersTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
                    HelpLayout()
                }
            }
        }
    }
}

@Composable
fun HelpLayout(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val actualPage = rememberSaveable { mutableStateOf(0) }

    if (isLandscape) {
        Horizontal(actualPage, context)
    } else {
        Vertical(actualPage, context)
    }
}
@Composable
private fun Horizontal(actualPage: MutableState<Int>, context: Context) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E3B4E))
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Help(actualPage)
            }
            Column (modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp)
                )
            }

        }
        Row (modifier = Modifier.padding(top = 16.dp)) {
            PersonalizedButton(stringResource(R.string.go_back), onClick = { (context as Activity).finish() })

        }
    }
}

@Composable
private fun Vertical(actualPage: MutableState<Int>, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E3B4E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Help(actualPage)

        PersonalizedButton(stringResource(R.string.go_back), onClick = { (context as Activity).finish() })
    }
}
@Composable
private fun Help(actualPage: MutableState<Int>) {
    val titles = listOf(
        stringResource(R.string.title_rules),
        stringResource(R.string.title_objective),
        stringResource(R.string.title_movement),
        stringResource(R.string.title_turns),
        stringResource(R.string.title_capture),
        stringResource(R.string.title_dame),
    )
    val rules = listOf(
        stringResource(R.string.text_rules),
        stringResource(R.string.text_objective),
        stringResource(R.string.text_movement),
        stringResource(R.string.text_turns),
        stringResource(R.string.text_capture),
        stringResource(R.string.text_dame)
    )

    Text(
        text = stringResource(R.string.how_game_works),
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Text(
        text = titles[actualPage.value],
        fontSize = 20.sp,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
    )

    Text(
        text = rules[actualPage.value],
        fontSize = 16.sp,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            enabled = actualPage.value > 0,
            onClick = { actualPage.value = (actualPage.value - 1).coerceAtLeast(0) }) {
            Text(stringResource(R.string.previous_page))
        }
        Text(
            text = "${actualPage.value + 1} / ${rules.size}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Button(
            enabled = actualPage.value < rules.size - 1,
            onClick = { actualPage.value = (actualPage.value + 1).coerceAtMost(rules.size - 1) }) {
            Text(stringResource(R.string.next_page))
        }
    }
}

