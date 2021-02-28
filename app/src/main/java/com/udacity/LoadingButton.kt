package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var currentState: Boolean = false

    /*Paint object*/
    private val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.default_text_size)
    }


    /*Dimens values*/
    private val clipRectRight = 950f
    private val clipRectTop = 40f
    private val clipRectLeft = 0f
    private val textSize = resources.getDimension(R.dimen.default_text_size)
    private val circleRadius = 30f

//    private var circleSweepAngle = CircleAngle.START


    private var widthSize = 0
    private var heightSize = 0

    private var fillWidth = 0f
    private var arcSweepAngle = 0f

    private var valueAnimator = ValueAnimator()


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (new == ButtonState.Loading) {
            fillButton()
            valueAnimator.start()
            invalidate()
        } else {
            valueAnimator.cancel()
        }

    }


    init {
        //ensure view is clickable
        isClickable = true
        buttonState = ButtonState.Start
    }

    override fun performClick(): Boolean {
        super.performClick()

        buttonState = ButtonState.Loading
        invalidate()
        return true

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawButton(canvas)

    }

    private fun drawButton(canvas: Canvas?) {
        var buttonText = ""
        var color = 0


        if (buttonState == ButtonState.Start) {
            //button rectangle
            color = Color.WHITE
            drawButtonRectangle(canvas, color)

            //text
            buttonText = resources.getText(R.string.download).toString()
            color = ContextCompat.getColor(context, R.color.secondaryColor)
            drawText(canvas, buttonText, color)

        }
        if (buttonState == ButtonState.Loading) {
            //paint over button
            color = R.color.secondaryColor
            drawButtonRectangle(canvas, ContextCompat.getColor(context, color))

            //text
            buttonText = resources.getString(R.string.button_loading)
            color = ContextCompat.getColor(context, R.color.primaryTextColor)
            drawText(canvas, buttonText, color)


            //Loading arc
            color = ContextCompat.getColor(context, R.color.primaryLightColor)
            drawRotatingArc(canvas, color)

        }

        if (buttonState == ButtonState.Completed) {
            //paint over button
            color = R.color.secondaryColor
            drawButtonRectangle(canvas, ContextCompat.getColor(context, color))
            //text
            buttonText = resources.getString(R.string.done)
            color = ContextCompat.getColor(context, R.color.primaryTextColor)
            drawText(canvas, buttonText, color)
            currentState = true
        }

    }

    private fun drawRotatingArc(canvas: Canvas?, color: Int) {
        paint.color = color
        val arcRect = RectF(
            widthSize.toFloat() - (3 * circleRadius),
            ((heightSize / 1.6) - circleRadius).toFloat(),
            widthSize.toFloat() - circleRadius,
            (heightSize / 1.6 + circleRadius).toFloat()
        )
        canvas?.drawArc(
            arcRect,
            0f,
            arcSweepAngle,
            true, paint
        )
    }

    private fun drawText(canvas: Canvas?, text: String, textColor: Int) {
        paint.color = textColor
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        canvas?.drawText(
            text,
            (widthSize / 2).toFloat(), (heightSize / 1.45).toFloat(), paint
        )
    }

    private fun drawButtonRectangle(canvas: Canvas?, color: Int) {
        paint.color = color
        val rectF = RectF(clipRectLeft, clipRectTop, widthSize.toFloat(), heightSize.toFloat())
        canvas?.drawRoundRect(rectF, clipRectRight / 4, clipRectRight / 4, paint)
    }

    private fun fillButton() {
        //change button color
        paint.color = ContextCompat.getColor(context, R.color.primaryColor)
        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
            duration = 3000
            addUpdateListener { valueAnimator ->
                valueAnimator.repeatCount = 2
                valueAnimator.repeatMode = ValueAnimator.RESTART
                fillWidth = valueAnimator.animatedValue as Float
                arcSweepAngle = fillWidth / widthSize * 360
                invalidate()
            }
        }
    }

    //Start download
    fun downloadBegin() {
        buttonState = ButtonState.Loading
    }

    //Download complete
    fun downloadComplete() {
        buttonState = ButtonState.Completed
    }
}
