package com.example.simpleintervaltimer.common.helper

fun Long.getDisplayMillis() = this % 1000
fun Long.getDisplaySeconds() = this / 1000 % 60
fun Long.getDisplayMinutes() = this / (1000 * 60) % 60
