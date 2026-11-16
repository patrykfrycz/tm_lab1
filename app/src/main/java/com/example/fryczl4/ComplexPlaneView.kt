package com.example.fryczl4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.max

class ComplexPlaneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var resultRe: Double? = null
    private var resultIm: Double? = null

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.indygo)
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.indygo)
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.indygo)
        textSize = 36f
        textAlign = Paint.Align.CENTER
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.morski_noca)
        style = Paint.Style.FILL
    }

    fun setResult(re: Double, im: Double) {
        resultRe = re
        resultIm = im
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2f
        val cy = h / 2f

        // Draw axes (X and Y intersecting at center, NO grids)
        canvas.drawLine(0f, cy, w, cy, axisPaint) // X axis
        canvas.drawLine(cx, 0f, cx, h, axisPaint) // Y axis

        // Hollow arrowheads on axes (V-shape stroke paths)
        val arrowSize = 16f

        // X axis right arrow (hollow V-shape)
        val xArrowPath = Path().apply {
            moveTo(w - arrowSize * 1.5f, cy - arrowSize)
            lineTo(w, cy)
            lineTo(w - arrowSize * 1.5f, cy + arrowSize)
        }
        canvas.drawPath(xArrowPath, arrowPaint)

        // Y axis top arrow (hollow V-shape)
        val yArrowPath = Path().apply {
            moveTo(cx - arrowSize, arrowSize * 1.5f)
            lineTo(cx, 0f)
            lineTo(cx + arrowSize, arrowSize * 1.5f)
        }
        canvas.drawPath(yArrowPath, arrowPaint)

        // Axis labels near arrowheads
        textPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Re", w - 20f, cy - 15f, textPaint)

        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("Im", cx + 20f, 35f, textPaint)

        // Plot result point if available with auto-scaling
        val re = resultRe
        val im = resultIm
        if (re != null && im != null) {
            val maxCoord = max(max(abs(re), abs(im)), 2.0) * 1.3
            val scale = minOf(cx, cy) / maxCoord.toFloat()

            val px = cx + (re.toFloat() * scale)
            val py = cy - (im.toFloat() * scale)

            // Point indicating the result
            canvas.drawCircle(px, py, 12f, pointPaint)

            // Text showing coordinates near point
            textPaint.textAlign = Paint.Align.CENTER
            val formatted = formatComplexPoint(re, im)
            canvas.drawText(formatted, px, py - 20f, textPaint)
        }
    }

    private fun formatComplexPoint(re: Double, im: Double): String {
        return when {
            im == 0.0 -> "$re"
            re == 0.0 -> "${im}i"
            im > 0 -> "$re + ${im}i"
            else -> "$re - ${-im}i"
        }
    }
}
