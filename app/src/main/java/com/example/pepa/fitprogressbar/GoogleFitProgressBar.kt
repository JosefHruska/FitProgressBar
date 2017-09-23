package com.example.pepa.fitprogressbar

import android.content.Context
import android.util.AttributeSet

/**
 * Created by pepa on 23/09/2017.
 */


class GoogleFitProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseProgressBar(context, attrs, defStyleAttr) {

    override fun getLayoutRes() = R.layout.google_fit_progress_bar

}