package com.summer.core.util

fun Float.roundToTwoDecimalPlaces(): Float {
    return (Math.round(this * 100) / 100.0).toFloat()
}


fun main() {
    val number = 3.14159f
    val roundedNumber = number.roundToTwoDecimalPlaces()
    println("Rounded Number: $roundedNumber") // Output: Rounded Number: 3.14
}