package com.summer.core.ui

import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.summer.core.R

object DataBindingAdapters {
    @JvmStatic
    @BindingAdapter("htmlText")
    fun AppCompatTextView.setHtmlText(html: String?) {
        html?.let {
            text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
        }
    }

    @JvmStatic
    @BindingAdapter("setDrawableRes")
    fun AppCompatImageView.setDrawableRes(@DrawableRes drawable: Int) {
        setImageResource(drawable)
    }

    @JvmStatic
    @BindingAdapter(
        "app:layout_marginAll",
        "app:layout_marginHorizontal",
        "app:layout_marginVertical",
        "app:layout_marginTop",
        "app:layout_marginBottom",
        "app:layout_marginStart",
        "app:layout_marginEnd",
        requireAll = false
    )
    fun View.setLayoutMargins(
        marginAll: Float?,
        marginHorizontal: Float?,
        marginVertical: Float?,
        marginTop: Float?,
        marginBottom: Float?,
        marginStart: Float?,
        marginEnd: Float?
    ) {
        val layoutParams = layoutParams as? ViewGroup.MarginLayoutParams ?: return

        val spacingNone = resources.getDimensionPixelSize(R.dimen.spacing_nothing)

        if (marginAll != null) {
            layoutParams.setMargins(
                marginAll.toInt(), marginAll.toInt(), marginAll.toInt(), marginAll.toInt()
            )
        } else {
            val resolvedStart = marginStart?.toInt() ?: marginHorizontal?.toInt() ?: spacingNone
            val resolvedEnd = marginEnd?.toInt() ?: marginHorizontal?.toInt() ?: spacingNone
            val resolvedTop = marginTop?.toInt() ?: marginVertical?.toInt() ?: spacingNone
            val resolvedBottom = marginBottom?.toInt() ?: marginVertical?.toInt() ?: spacingNone

            layoutParams.setMargins(resolvedStart, resolvedTop, resolvedEnd, resolvedBottom)
        }

        this.layoutParams = layoutParams
    }
}