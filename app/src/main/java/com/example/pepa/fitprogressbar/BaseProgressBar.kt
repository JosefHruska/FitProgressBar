package com.example.pepa.fitprogressbar

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_test.view.*
import kotlinx.android.synthetic.main.google_fit_progress_bar.view.*

/**
 * Created by pepa on 23/09/2017.
 */

abstract class BaseProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {



    protected var totalWidth = 0
    protected var progressPercentage = 0
    protected var progressColor = 0
    protected var cornerRadious = 0
    protected var progressPadding = 0

    protected var progressBackgroundColor = 0

    protected lateinit var progressBar: LinearLayout
    protected lateinit var progressBackground: LinearLayout


    init {
        setup(context, attrs)
    }

    abstract fun getLayoutRes(): Int

    // Attributes for child views may be extracted here.
    open fun setupChildViewAttributes(context: Context, attrs: AttributeSet) {

    }

    // Additional views may be setup here.
    open fun setupChildViews() {

    }

    private fun setup(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            setupAttributes(context, attrs)
        }
        removeAllViews()
        // Setup layout for sub class
        LayoutInflater.from(context).inflate(getLayoutRes(), this)
        // Initial default view

        //layoutBackground = findViewById(R.id.layout_background) as LinearLayout
        progressBar = vProgressBar
        progressBackground = vProgressBackground

        //layoutSecondaryProgress = findViewById(R.id.layout_secondary_progress) as LinearLayout

        setupChildViews()
    }

    private fun setupAttributes(context: Context, attrs: AttributeSet) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.BaseProgressBar)
        progressPercentage = attributes.getInt(R.styleable.BaseProgressBar_progressPercentage, 50)
        cornerRadious = attributes.getInt(R.styleable.BaseProgressBar_cornerRadius, DEFAULT_PROGRESS_RADIUS)
        progressColor = attributes.getInt(R.styleable.BaseProgressBar_progressColor, DEFAULT_PROGRESS_COLOR)
        progressPadding = attributes.getDimensionPixelSize(R.styleable.BaseProgressBar_progressPadding, DEFAULT_PROGRESS_PADDING)
        progressBackgroundColor = attributes.getInt(R.styleable.BaseProgressBar_progressColor, DEFAULT_PROGRESS_COLOR)

        attributes.recycle()

        setupChildViewAttributes(context, attrs)
    }


    // Progress bar always refresh when view size has changed
    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight)
        if (!isInEditMode) {
            totalWidth = newWidth
            drawAll()
            postDelayed({
                drawPrimaryProgress()
                drawSecondaryProgress()
            }, 100)
        }
    }

    // Redraw all view
    protected fun drawAll() {
     //   drawBackgroundProgress()
       // drawPadding()
       // drawProgressReverse()
        drawPrimaryProgress()
        drawSecondaryProgress()
        //onViewDraw()
    }


    private fun drawPrimaryProgress() {
        drawProgress(progressBar, progressPercentage, totalWidth.toFloat(), cornerRadious, progressPadding, progressColor)
    }

    private fun drawSecondaryProgress() {
       // drawProgress(layoutSecondaryProgress, max, secondaryProgress, totalWidth.toFloat(), radius, padding, colorSecondaryProgress, isReverse)
    }

    // Draw progress background
    private fun drawBackgroundProgress() {
        val backgroundDrawable = createGradientDrawable(progressBackgroundColor)
        val newRadius = cornerRadious - progressPadding / 2
        backgroundDrawable.cornerRadii = floatArrayOf(newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            progressBackground.setBackground(backgroundDrawable)
        } else {
            progressBackground.setBackgroundDrawable(backgroundDrawable)
        }
    }


    protected fun drawProgress(layoutProgress: LinearLayout, progress: Int, totalWidth: Float,
                               radius: Int, padding: Int, colorProgress: Int, progressMaximum: Int = 100) {
        val backgroundDrawable = createGradientDrawable(colorProgress)
        val newRadius = radius - padding / 2
        backgroundDrawable.cornerRadii = floatArrayOf(newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat())
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layoutProgress.background = backgroundDrawable
        } else {
            layoutProgress.setBackgroundDrawable(backgroundDrawable)
        }


        val ratio = progressMaximum / progress
        // Set progress width
        val progressWidth = ((totalWidth - padding * 2) / ratio).toInt()
        val progressParams = layoutProgress.layoutParams
        progressParams.width = progressWidth
        layoutProgress.layoutParams = progressParams
    }


    // Create an empty color rectangle gradient drawable
    protected fun createGradientDrawable(color: Int): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.setColor(color)
        return gradientDrawable
    }


    companion object {
        const val DEFAULT_MAX_PROGRESS = 100
        const val DEFAULT_PROGRESS = 0
        const val DEFAULT_SECONDARY_PROGRESS = 0
        const val DEFAULT_PROGRESS_RADIUS = 30
        const val DEFAULT_PROGRESS_COLOR = R.color.colorAccent
        const val DEFAULT_BACKGROUND_COLOR = R.color.colorBackground

        const val DEFAULT_PROGRESS_PADDING = 8
        const val DEFAULT_BACKGROUND_PADDING = 0
    }

}
