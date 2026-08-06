package com.example.ppgandroidexample.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ppgandroidexample.ui.theme.PPGAndroidExampleTheme
import com.pushpushgo.sdk.PushPushGo


@Composable
fun SplashScreenContent() {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color.Black,
            Color.Cyan,
            Color.Black,
        ),
    )

    PPGAndroidExampleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PPG",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var navController: NavHostController

    private val splashDisplayLength: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // A Live Activity click opens the launcher activity - which is this one -
        // with the click details as intent extras. This reports the click analytics
        // (body tap and action buttons are told apart automatically) and opens the
        // deep link through `notificationHandler` (see Application.kt).
        PushPushGo.getInstance().handleLiveActivityClick(intent)

        setContent {
            navController = rememberNavController()
            SplashScreenContent()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val mainIntent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, splashDisplayLength)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        PushPushGo.getInstance().handleLiveActivityClick(intent)
    }
}
