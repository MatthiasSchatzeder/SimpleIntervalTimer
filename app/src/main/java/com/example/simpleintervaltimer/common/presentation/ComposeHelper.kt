package com.example.simpleintervaltimer.common.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext


private fun Context.findActivity(): Activity? = when (this) {
	is Activity -> this
	is ContextWrapper -> baseContext.findActivity()
	else -> null
}

/**
 * @see <a href="https://stackoverflow.com/questions/69230049/how-to-force-orientation-for-some-screens-in-jetpack-compose"> StackOverflow </a>
 */
@Composable
fun LockScreenOrientation(orientation: Int) {
	val context = LocalContext.current
	DisposableEffect(orientation) {
		val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
		val originalOrientation = activity.requestedOrientation
		activity.requestedOrientation = orientation
		onDispose {
			// restore original orientation when view disappears
			activity.requestedOrientation = originalOrientation
		}
	}
}

/**
 * @see <a href="https://stackoverflow.com/questions/69039723/is-there-a-jetpack-compose-equivalent-for-androidkeepscreenon-to-keep-screen-al"> StackOverflow </a>
 */
@Composable
fun KeepScreenOn() {
	val context = LocalContext.current
	DisposableEffect(Unit) {
		val window = context.findActivity()?.window ?: return@DisposableEffect onDispose {}
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		onDispose {
			window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		}
	}
}
