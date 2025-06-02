package com.example.checkers.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkers.R
import com.example.checkers.components.GeneralAppBar
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
                Scaffold(
                    topBar = { GeneralAppBar(stringResource(R.string.game_results)) },
                    containerColor = Color(0xFF2E3B4E)
                ) {  paddingValues ->
                    MyApp(alias, result, userTeam, userPieces, cpuPieces, movements, time, finalizationDate, paddingValues)
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
    finalizationDate: String,
    paddingValues: PaddingValues
) {
    val email = rememberSaveable { mutableStateOf("ecp16@alumnes.udl.cat") }
    var body = stringResource(R.string.result_msg, finalizationDate, alias, userTeam, result, movements, userPieces, cpuPieces)
    if (time != "") {
        body += stringResource(R.string.time_involved, time)
    } else {
        body += stringResource(R.string.no_time)
    }
    val text = rememberSaveable { mutableStateOf(body) }
    val finalization = rememberSaveable { mutableStateOf(finalizationDate) }
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        HorizontalApp(
            context = context,
            alias = alias,
            result = result,
            userTeam = userTeam,
            userPieces = userPieces,
            cpuPieces = cpuPieces,
            movements = movements,
            time = time,
            finalization = finalization,
            email = email,
            text = text,
            paddingValues
        )
    } else {
        VerticalApp(
            context = context,
            alias = alias,
            result = result,
            userTeam = userTeam,
            userPieces = userPieces,
            cpuPieces = cpuPieces,
            movements = movements,
            time = time,
            finalization = finalization,
            email = email,
            text = text,
            paddingValues
        )
    }

}

@Composable
private fun VerticalApp(
    context: Context,
    alias: String,
    result: String,
    userTeam: String,
    userPieces: Int,
    cpuPieces: Int,
    movements: Int,
    time: String,
    finalization: MutableState<String>,
    email: MutableState<String>,
    text: MutableState<String>,
    paddingVal: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E3B4E))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(paddingVal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),

        ) {
        Results(alias, result, userTeam, userPieces, cpuPieces, movements, time, finalization)

        EditableData(email, text)

        FancyButton(stringResource(R.string.send)) {
            sendMail(
                email.value,
                text.value,
                "Log " + finalization.value,
                context
            )
        }
        FancyButton(stringResource(R.string.new_game)) { playAgain(context) }
        FancyButton(stringResource(R.string.exit)) { (context as? Activity)?.finishAffinity() }

    }
}

@Composable
private fun HorizontalApp(
    context: Context,
    alias: String,
    result: String,
    userTeam: String,
    userPieces: Int,
    cpuPieces: Int,
    movements: Int,
    time: String,
    finalization: MutableState<String>,
    email: MutableState<String>,
    text: MutableState<String>,
    padding: PaddingValues
) {

    Column (
        modifier =  Modifier
            .fillMaxSize()
            .background(Color(0xFF2E3B4E))
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                Results(alias, result, userTeam, userPieces, cpuPieces, movements, time, finalization)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditableData(email, text)
            }
        }
        FancyButton(stringResource(R.string.send)) {
            sendMail(
                email.value,
                text.value,
                "Log " + finalization.value,
                context
            )
        }
        FancyButton(stringResource(R.string.new_game)) { playAgain(context) }
        FancyButton(stringResource(R.string.exit)) { (context as? Activity)?.finishAffinity() }
    }

}
@Composable
private fun Results(
    alias: String,
    result: String,
    userTeam: String,
    userPieces: Int,
    cpuPieces: Int,
    movements: Int,
    time: String,
    finalization: MutableState<String>
) {


    ResultItem(stringResource(R.string.alias), alias)
    ResultItem(stringResource(R.string.result), result)
    ResultItem(stringResource(R.string.user_team), userTeam)
    ResultItem(stringResource(R.string.user_pieces), "$userPieces")
    ResultItem(stringResource(R.string.cpu_pieces), "$cpuPieces")
    ResultItem(stringResource(R.string.movements), "$movements")
    ResultItem(
        stringResource(R.string.time),
        if (time == "") stringResource(R.string.no_time) else time
    )
    ResultTextField(stringResource(R.string.time_finalization), finalization)
}

@Composable
private fun EditableData(
    email: MutableState<String>,
    text: MutableState<String>
) {
    TextField(
        value = email.value,
        onValueChange = { email.value = it },
        label = { Text(stringResource(R.string.email_placeholder)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth()
    )

    TextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text(stringResource(R.string.email_body)) },
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
private fun ResultItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(end = 16.dp))
        Text(value, fontSize = 18.sp, color = Color.White)
    }
}
@Composable
private fun ResultTextField(label: String, txt: MutableState<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(0.95f),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        TextField(
            value = txt.value,
            onValueChange = { txt.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        )
    }
}

@Composable
private fun FancyButton(txt: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.95f),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0A43C))
    ) {
        Text(txt, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

private fun sendMail(email:String, body: String, subject: String, context: Context) {


    val mailUri = Uri.Builder()
        .scheme("mailto")
        .authority(email)
        .appendQueryParameter("subject", subject)
        .appendQueryParameter("body", body)
        .build()

    val emailIntent = Intent(Intent.ACTION_SENDTO, mailUri)

    try {
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.sending_meail)))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, context.getString(R.string.email_not_found), Toast.LENGTH_LONG).show()
    }
}

private fun playAgain(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    context.startActivity(intent)
}