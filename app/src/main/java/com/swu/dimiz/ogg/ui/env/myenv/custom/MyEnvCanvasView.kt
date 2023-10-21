package com.swu.dimiz.ogg.ui.env.myenv.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.swu.dimiz.ogg.R
import kotlin.math.abs


class MyEnvCanvasView(context: Context): View(context) {

    private lateinit var envCanvas: Canvas
    private lateinit var envBitmap: Bitmap
    private lateinit var imageView: ImageView

    private val drawColor = ResourcesCompat.getColor(resources, R.color.primary_blue, null)

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = drawColor
    }

    private val paintSecond = Paint().apply {
        style = Paint.Style.STROKE
        color = drawColor
    }

    private var path = Path()

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var imageWidth = 40
    private var imageHeight = 40

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHight)

        if (::envBitmap.isInitialized)
            envBitmap.recycle()

        envBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        envCanvas = Canvas(envBitmap)
        imageView.setImageResource(R.drawable.login_image_face)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(envBitmap, 0f, 0f, paintSecond)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when(event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)

        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {

        // 이동 거리를 계산
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)

        // 터치 반경보다 먼 경우 이동
        if(dx >= touchTolerance || dy >= touchTolerance) {
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX)/2,
                (motionTouchEventY + currentY)/2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            envCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    private fun touchUp() {
        path.reset()
    }
}