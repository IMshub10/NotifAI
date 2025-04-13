package com.summer.core.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Returns the screen width in pixels.
 *
 * @param activity The activity from which to get the screen width.
 * @return The screen width in pixels.
 */
fun getScreenWidthIntDp(activity: Activity): Int {
    val displayMetrics = DisplayMetrics()
    activity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

/**
 * Returns the screen height in pixels.
 *
 * @param activity The activity from which to get the screen height.
 * @return The screen height in pixels.
 */
fun getScreenHeightIntDp(activity: Activity): Int {
    val displayMetrics = DisplayMetrics()
    activity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

/**
 * Converts pixels to density-independent pixels (dp).
 *
 * @param context The context to use for getting the display metrics.
 * @return The converted value in dp.
 */
fun Int.pxToDp(context: Context): Float {
    val density = context.resources.displayMetrics.density
    return this / density
}

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * Shows a short Toast message.
 *
 * @param message The message to display in the Toast.
 */
fun Activity.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Shows a short Toast message.
 *
 * @param message The message to display in the Toast.
 */
fun Fragment.showShortToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
