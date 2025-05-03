package com.example.checkers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkers.ui.theme.CheckersTheme

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckersTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
                    App()
                }
            }
        }
    }
}

fun navigate(context: Context, cls: Class<*>) {
    val intent = Intent(context, cls)
    context.startActivity(intent)
}

@Composable
private fun App(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2E3B4E)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if(isLandscape) Arrangement.SpaceEvenly else Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        PersonalizedButton(stringResource(R.string.help_txt), { navigate(context, HelpActivity::class.java) })
        PersonalizedButton(stringResource(R.string.start_game), { navigate(context, MainActivity::class.java) })
        PersonalizedButton(stringResource(R.string.exit), { (context as? Activity)?.finish() })
    }
}

@Composable
fun PersonalizedButton(txt: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0A43C))
    ) {
        Text(txt, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }

    Spacer(modifier = Modifier.height(12.dp))
}