package com.metehanbolat.customviewsxml

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat

class OTPViewCustom @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var spaceBetweenCircles = 36f
    private var numberOfCircle = 6f
    private var clickListener: OnClickListener? = null

    private var onInputDigitsListener: ((String) -> Unit)? = null

    private var emptyCirclePaint: Paint? = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = textSize
        color = ContextCompat.getColor(context, R.color.light_gray)
    }

    private var filledCirclePaint: Paint? = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = textSize
        color = ContextCompat.getColor(context, R.color.gray)
    }

    private var otpBackgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.white)
    }

    private var otpBackgroundStrokePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val linePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.red)
        strokeWidth = 2f
    }

    companion object {
        private const val MAX_LENGTH = 6
    }

    init {
        val density = context.resources.displayMetrics.density
        spaceBetweenCircles *= density

        filters += InputFilter.LengthFilter(MAX_LENGTH)

        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        DigitsKeyListener.getInstance("1234567890")
        isFocusable = true
        setTextIsSelectable(false)

        super.setOnClickListener { view ->
            setSelection(text?.length ?: 0)
            clickListener?.onClick(view)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minHeight = (60 * resources.displayMetrics.density).toInt()
        setMeasuredDimension(measuredWidth, maxOf(minHeight, measuredHeight))
    }

    override fun setOnClickListener(l: OnClickListener?) {
        clickListener = l
    }

    override fun onDraw(canvas: Canvas?) {
        val measuredHeightF = measuredHeight.toFloat()
        val measuredWidthF = measuredWidth.toFloat()

        canvas?.drawRoundRect(
            0f,
            0f,
            measuredWidthF,
            measuredHeightF,
            15f,
            15f,
            otpBackgroundPaint
        )

        canvas?.drawRoundRect(
            1f,
            1f,
            measuredWidthF - 1f,
            measuredHeightF - 1f,
            15f,
            15f,
            otpBackgroundStrokePaint
        )

        (0 until numberOfCircle.toInt()).forEach {
            val circleSize = (measuredHeight / 2.8f) / resources.displayMetrics.density
            val bet = circleSize + spaceBetweenCircles
            val defaultLeftPadding = (measuredWidth - ((numberOfCircle - 1) * circleSize) - ((numberOfCircle - 1) * spaceBetweenCircles)) / 2
            val circlePosition = (defaultLeftPadding + (it * bet))

            canvas?.drawCircle(
                circlePosition,
                measuredHeightF / 2,
                circleSize,
                emptyCirclePaint!!
            )

            if (it != numberOfCircle.toInt() - 1) {
                canvas?.drawLine(
                    defaultLeftPadding + circleSize / 2 + (spaceBetweenCircles / 2) + (it * (spaceBetweenCircles + circleSize)),
                    measuredHeight / 3.5f,
                    defaultLeftPadding + circleSize / 2 + (spaceBetweenCircles / 2) + (it * (spaceBetweenCircles + circleSize)),
                    measuredHeight - (measuredHeight / 3.5f),
                    linePaint
                )
            }

            if (isFocused && it == text!!.length) {
                canvas?.drawCircle(
                    circlePosition,
                    measuredHeightF / 2,
                    circleSize,
                    emptyCirclePaint!!
                )
            }
            if (text!!.length > it) {
                canvas?.drawCircle(
                    circlePosition,
                    measuredHeightF / 2,
                    circleSize,
                    filledCirclePaint!!
                )
            }
        }
    }

    fun setOnInputDigitsListener(listener: (String) -> Unit) {
        onInputDigitsListener = listener
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (text?.length == 6) {
            onInputDigitsListener?.invoke(text.toString())
        }
    }

}