package com.example.pepa.fitprogressbar

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.google_fit_progress_bar.view.*

/**
 * Created by pepa on 23/09/2017.
 */

abstract class BaseProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    protected var totalWidth = 0
    protected var totalHeight = 0
    protected var progressPercentage = 0
    protected var progressColorRes = 0
    protected var cornerRadius = 0
    protected var progressPadding = 0
    protected var progressIconHeight = 0

    protected var progressBackgroundColor = 0

    protected lateinit var progressBar: LinearLayout
    protected lateinit var progressBackground: LinearLayout

    init { setup(context, attrs) }

    /**
     * Get the layout resources from child classes
     *
     * @return reference to layout resource file of progressBar
     */
    abstract fun getLayoutRes(): Int

    /**
     *  Custom attributes for views in child classes should be extracted here.
     */
    open fun setupChildViewAttributes(context: Context, attrs: AttributeSet) {}

    /**
     *  You may setup some things like listeners, background etc. here (before the view is drawn).
     */
    open fun setupChildViews() {}

    /**
     *  You may draw views of child classes here. All views in general should be drawn before progressBar view.
     *  If there is some reason to draw the additional view after progressBar view, you should use drawChildViewsDelayed() instead.
     */
    open fun drawChildViews() {

    }

    /**
     *  You may draw views of child classes here, if they have to be drawn after progressBar view.
     *  If there is no special reason to draw views before progressBar view, you should use drawChildViews() instead.
     *
     * @see #drawChildViews()
     */
    open fun drawChildViewsDelayed() {}

    private fun setup(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            setupAttributes(context, attrs)
        }
        removeAllViews()

        // Get the layout resources from child classes
        LayoutInflater.from(context).inflate(getLayoutRes(), this)

        // Initial default views
        progressBar = vProgressBar
        progressBackground = vProgressBackground

        setupChildViews()
    }

    private fun setupAttributes(context: Context, attrs: AttributeSet) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.BaseProgressBar)
        cornerRadius = attributes.getInt(R.styleable.BaseProgressBar_cornerRadius, 30)
        progressPadding = attributes.getDimension(R.styleable.BaseProgressBar_progressPadding, 16F).toInt()
        progressColorRes = attributes.getResourceId(R.styleable.BaseProgressBar_progressColorRes,R.color.colorAccent)
        progressBackgroundColor = attributes.getResourceId(R.styleable.BaseProgressBar_progressBackgroundColor,  R.color.colorBackground)
        progressIconHeight = attributes.getDimension(R.styleable.BaseProgressBar_progressIconDimensions, 16F).toInt()
        progressPercentage = attributes.getInt(R.styleable.BaseProgressBar_progressPercentage, 50)

        attributes.recycle()

        setupChildViewAttributes(context, attrs)
    }


    // Progress bar is always refreshed when view size has changed
    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight)
        if (!isInEditMode) {
            totalWidth = newWidth
            totalHeight = newHeight
            drawAll()
            postDelayed({
                drawPrimaryProgress()
                drawChildViewsDelayed()
            }, 100)
        }
    }

    // Redraw all view
    protected fun drawAll() {
        drawBackgroundProgress()
        drawPadding()
        drawPrimaryProgress()
        drawChildViews()
    }


    private fun drawPrimaryProgress() {
        drawProgress(progressBar, progressPercentage, totalWidth.toFloat(), cornerRadius, progressPadding, progressColorRes)
    }

    // Draw progress background
    private fun drawBackgroundProgress() {
        val backgroundDrawable = createGradientDrawable(context.resources.getColor(progressBackgroundColor))
        val newRadius = cornerRadius - progressPadding / 2
        backgroundDrawable.cornerRadii = floatArrayOf(newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            progressBackground.background = backgroundDrawable
        } else {
            progressBackground.setBackgroundDrawable(backgroundDrawable)
        }
    }

    protected fun drawProgress(layoutProgress: LinearLayout, progress: Int, totalWidth: Float,
                               radius: Int, padding: Int, colorProgress: Int, progressMaximum: Int = 100) {
        val backgroundDrawable = createGradientDrawable(context.resources.getColor(colorProgress))
        val newRadius = radius - padding / 2
        backgroundDrawable.cornerRadii = floatArrayOf(newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat(), newRadius.toFloat())
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layoutProgress.background = backgroundDrawable
        } else {
            layoutProgress.setBackgroundDrawable(backgroundDrawable)
        }

        val ratio = progressMaximum / progress
        // Calculate the width of progressbar - since it is build with constraint layout, we have to calculate also the height.
        val progressWidth = ((totalWidth - padding * 2) / ratio).toInt()
        val progressHeight = (totalHeight - padding * 2)
        val progressParams = layoutProgress.layoutParams
        progressParams.width = progressWidth
        progressParams.height = progressHeight
        layoutProgress.layoutParams = progressParams
    }

    protected fun drawPadding() {
        progressBackground.setPadding(progressPadding, progressPadding, progressPadding, progressPadding)
    }

    fun setProgress(progress: Int) {
        progressPercentage = progress
    }

    fun calculateAndSetProgress(currentValue: Int, maxValue: Int) {
        val ratio = currentValue / maxValue
        progressPercentage = ratio * 100
    }

    // Create an empty color rectangle gradient drawable
    private fun createGradientDrawable(color: Int): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.setColor(color)
        return gradientDrawable
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)

        ss.cornerRadius = this.cornerRadius
        ss.progressPadding = this.progressPadding

        ss.progressBackgroundColor = this.progressBackgroundColor
        ss.progressColorRes = this.progressColorRes

        ss.progressPercentage = this.progressPercentage

        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        progressPercentage = state.progressPercentage

        cornerRadius = state.cornerRadius
        progressPadding = state.progressPadding

        progressBackgroundColor = state.progressBackgroundColor
        progressColorRes = state.progressColorRes
    }

    private class SavedState : View.BaseSavedState {
        internal var progressPercentage: Int = 0

        internal var cornerRadius: Int = 0
        internal var progressPadding: Int = 0

        internal var progressBackgroundColor: Int = 0
        internal var progressColorRes: Int = 0


        internal constructor(superState: Parcelable) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            this.progressPercentage = parcel.readInt()

            this.cornerRadius = parcel.readInt()
            this.progressPadding = parcel.readInt()

            this.progressBackgroundColor = parcel.readInt()
            this.progressColorRes = parcel.readInt()

        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(this.progressPercentage)

            out.writeInt(this.cornerRadius)
            out.writeInt(this.progressPadding)

            out.writeInt(this.progressBackgroundColor)
            out.writeInt(this.progressColorRes)

        }

        companion object {

            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)

                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }
}
