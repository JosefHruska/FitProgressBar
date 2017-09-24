package com.example.pepa.fitprogressbar

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import kotlinx.android.synthetic.main.google_fit_progress_bar.view.*

/**
 * Simple progress bar which is inspired by Google Fit progress bar.
 */


class GoogleFitProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseProgressBar(context, attrs, defStyleAttr) {

    override fun getLayoutRes() = R.layout.google_fit_progress_bar

    override fun drawChildViews() {
        drawProgressIcon()
    }

    fun setProgressText(text: String) {
        vProgressText.text = text
    }

    private fun drawProgressIcon() {
        val iconLayoutParameters = vProgressIcon.layoutParams
        iconLayoutParameters.width = progressIconHeight
        iconLayoutParameters.height = progressIconHeight
        vProgressIcon.layoutParams = iconLayoutParameters
    }

}