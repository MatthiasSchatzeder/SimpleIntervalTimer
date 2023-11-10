package com.example.simpleintervaltimer

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val time = mutableStateOf(10000L)
        val timer = object : CountDownTimer(10000L, 10L) {
            override fun onTick(millisUntilFinished: Long) {
                time.value = millisUntilFinished
            }
            override fun onFinish() {
                time.value = 0
            }
        }
        timer.start()

        setContent {
            SimpleintervaltimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Timer(time.value)
                }
            }
        }
    }
}

@Composable
fun Timer(timeMillis: Long) {
    val millis: Long = timeMillis % 1000
    val second: Long = timeMillis / 1000 % 60
    val minute: Long = timeMillis / (1000 * 60) % 60
    val timeText = "%d,%d".format(second, millis / 10)
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.align(Alignment.Center), text = timeText,
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    SimpleintervaltimerTheme {
        Timer(9500L)
    }
}