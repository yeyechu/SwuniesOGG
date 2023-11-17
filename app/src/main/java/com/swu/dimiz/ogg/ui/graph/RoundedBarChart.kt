package com.swu.dimiz.ogg.ui.graph

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChart(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) :
    BarChartRenderer(chart, animator, viewPortHandler) {
    private var mRadius = 30f

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans: Transformer = mChart.getTransformer(dataSet.axisDependency)
        mShadowPaint.color = dataSet.barShadowColor
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY
        if (mBarBuffers != null) {
            // initialize the buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setBarWidth(mChart.barData.barWidth)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.feed(dataSet)
            trans.pointValuesToPixel(buffer.buffer)

            // if multiple colors
            if (dataSet.colors.size > 1) {
                var j = 0
                while (j < buffer.size()) {
                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                        j += 4
                        continue
                    }
                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                    if (mChart.isDrawBarShadowEnabled) {
                        if (mRadius > 0) c.drawRoundRect(
                            RectF(
                                buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2], mViewPortHandler.contentBottom()
                            ), mRadius, mRadius, mShadowPaint
                        ) else c.drawRect(
                            buffer.buffer[j], mViewPortHandler.contentTop(),
                            buffer.buffer[j + 2], mViewPortHandler.contentBottom(), mShadowPaint
                        )
                    }

                    // Set the color for the currently drawn value. If the index
                    // is
                    // out of bounds, reuse colors.
                    mRenderPaint.color = dataSet.getColor(j / 4)
                    if (mRadius > 0) {
                        val path: Path = roundedRect(
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            buffer.buffer[j + 2],
                            buffer.buffer[j + 3], 15f, 15f, tl = true, tr = true, br = false, bl = false
                        )
                        c.drawPath(path, mRenderPaint)
                    } else c.drawRect(
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        buffer.buffer[j + 2], buffer.buffer[j + 3], mRenderPaint
                    )
                    j += 4
                }
            } else {
                mRenderPaint.color = dataSet.color
                var j = 0
                while (j < buffer.size()) {
                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                        j += 4
                        continue
                    }
                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                    if (mChart.isDrawBarShadowEnabled) {
                        if (mRadius > 0) c.drawRoundRect(
                            RectF(
                                buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()
                            ), mRadius, mRadius, mShadowPaint
                        ) else c.drawRect(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint
                        )
                    }
                    if (mRadius > 0) {
                        val path: Path = roundedRect(
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            buffer.buffer[j + 2],
                            buffer.buffer[j + 3], 15f, 15f, tl = true, tr = true, br = false, bl = false
                        )
                        c.drawPath(path, mRenderPaint)
                    } else c.drawRect(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint
                    )
                    j += 4
                }
            }
        }
    }

    companion object {
        fun roundedRect(
            left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float,
            tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean
        ): Path {
            var dx = rx
            var dy = ry
            val path = Path()
            if (dx < 0) dx = 0f
            if (dy < 0) dy = 0f
            val width = right - left
            val height = bottom - top
            if (dx > width / 2) dx = width / 2
            if (dy > height / 2) dy = height / 2
            val widthMinusCorners = width - 2 * dx
            val heightMinusCorners = height - 2 * dy
            path.moveTo(right, top + dy)
            if (tr) path.rQuadTo(0F, -dy, -dx, -dy) //top-right corner
            else {
                path.rLineTo(0F, -dy)
                path.rLineTo(-dx, 0F)
            }
            path.rLineTo(-widthMinusCorners, 0F)
            if (tl) path.rQuadTo(-dx, 0F, -dx, dy) //top-left corner
            else {
                path.rLineTo(-dx, 0F)
                path.rLineTo(0F, dy)
            }
            path.rLineTo(0F, heightMinusCorners)
            if (bl) path.rQuadTo(0F, dy, dx, dy) //bottom-left corner
            else {
                path.rLineTo(0F, dy)
                path.rLineTo(dx, 0F)
            }
            path.rLineTo(widthMinusCorners, 0F)
            if (br) path.rQuadTo(dx, 0F, dx, -dy) //bottom-right corner
            else {
                path.rLineTo(dx, 0F)
                path.rLineTo(0F, -dy)
            }
            path.rLineTo(0F, -heightMinusCorners)
            path.close() //Given close, last lineto can be removed.
            return path
        }
    }
}