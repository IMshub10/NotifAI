package com.summer.core.util

import android.app.Activity
import android.content.Intent

fun startActivityWithClearTop(activity: Activity, clas: Class<*>?) {
    activity.startActivity(Intent(activity, clas).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    })
    activity.finish()
}