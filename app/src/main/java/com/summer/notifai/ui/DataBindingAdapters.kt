package com.summer.notifai.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.summer.notifai.R

object DataBindingAdapters {

    @JvmStatic
    @BindingAdapter(value = ["selectedBackgroundTint", "isIncoming"], requireAll = true)
    fun View.setSelectedBackgroundColorOnlyIfSelected(isSelected: Boolean, isIncoming: Boolean) {
        foreground = if (isSelected) {
            // Optional soft overlay like WhatsApp
            ContextCompat.getDrawable(context, R.drawable.bg_selected_overlay)
        } else {
            // Remove selection effect
            null
        }
    }
    private fun resolveThemeColor(attrRes: Int, context: Context): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        return if (theme.resolveAttribute(attrRes, typedValue, true)) {
            ContextCompat.getColor(context, typedValue.resourceId)
        } else {
            0 // fallback if not found
        }
    }
}