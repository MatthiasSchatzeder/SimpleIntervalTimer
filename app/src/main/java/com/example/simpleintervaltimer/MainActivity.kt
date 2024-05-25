package com.example.simpleintervaltimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.simpleintervaltimer.timer.Timer
import com.example.simpleintervaltimer.timer.data.TimeInterval
import com.example.simpleintervaltimer.ui.theme.Background
import com.example.simpleintervaltimer.ui.theme.SimpleintervaltimerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SimpleintervaltimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Background
                ) {
                    Timer(TimeInterval(5_000, 2_000, 2))
                }
            }
        }
    }
}
