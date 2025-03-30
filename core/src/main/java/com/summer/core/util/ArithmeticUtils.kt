package com.summer.core.util

fun Float.roundToTwoDecimalPlaces(): Float {
    return (Math.round(this * 100) / 100.0).toFloat()
}