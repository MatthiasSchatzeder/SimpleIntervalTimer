package com.example.simpleintervaltimer.timer.domain.models

import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.fromUri

data class TimerSoundDefinition(
	val preEndWorkIntervalSound: MediaItem,
	val endWorkIntervalSound: MediaItem,
	val preEndRestIntervalSound: MediaItem,
	val endRestIntervalSound: MediaItem,
	val finishSound: MediaItem
) {
	companion object {
		fun fromResourceIds(
			@RawRes preEndWorkIntervalSoundResId: Int,
			@RawRes endWorkIntervalSoundResId: Int,
			@RawRes preEndRestIntervalSoundResId: Int,
			@RawRes endRestIntervalSoundResId: Int,
			@RawRes finishSoundResId: Int
		): TimerSoundDefinition {
			return TimerSoundDefinition(
				preEndWorkIntervalSound = createMediaItemFromResourceId(preEndWorkIntervalSoundResId),
				endWorkIntervalSound = createMediaItemFromResourceId(endWorkIntervalSoundResId),
				preEndRestIntervalSound = createMediaItemFromResourceId(preEndRestIntervalSoundResId),
				endRestIntervalSound = createMediaItemFromResourceId(endRestIntervalSoundResId),
				finishSound = createMediaItemFromResourceId(finishSoundResId)
			)
		}

		private fun createMediaItemFromResourceId(@RawRes resourceId: Int): MediaItem {
			val uri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(resourceId.toString()).build()
			return fromUri(uri)
		}
	}
}
