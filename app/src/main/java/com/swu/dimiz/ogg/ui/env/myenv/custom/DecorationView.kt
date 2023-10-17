package com.swu.dimiz.ogg.ui.env.myenv.custom

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet

class DecorationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private var width = 0.0f
    private var height = 0.0f
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHight)
    }
}