package com.swu.dimiz.ogg.ui.env.stamp

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.swu.dimiz.ogg.R
import com.swu.dimiz.ogg.contents.listset.listutils.*
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition
import kotlin.math.abs

// ──────────────────────────────────────────────────────────────────────────────────────
//                                       스탬프 레이어
@BindingAdapter("expandButtonImage")
fun ImageView.setImage(boolean: Boolean) {
    boolean.let {
        if(boolean)
            setImageResource(R.drawable.common_button_arrow_up)
        else
            setImageResource(R.drawable.common_button_arrow_down)
    }
}

@BindingAdapter("userCo2")
fun TextView.setCo2(item: MyCondition?) {
    item?.let {
        text = (item.aim * CO2_WHOLE).toString()
    }
}

@BindingAdapter("userLeftCo2")
fun TextView.setLeftCo2(data: Float?) {
    data?.let {
        text = if (data > 0f) {
            resources.getString(R.string.stamplayout_text_body, data)
        } else {
            resources.getString(R.string.stamplayout_text_body_beyond, abs(data))
        }
    }
}

@BindingAdapter("todayStamp")
fun ImageView.setTodayImage(data: Float) {
    when(data) {
        0f -> setBackgroundResource(R.drawable.env_image_stamp_000)
        in 0.001f..0.99f -> setBackgroundResource(R.drawable.env_image_stamp_050)
        else -> setBackgroundResource(R.drawable.env_image_stamp_100)
    }
}

// ──────────────────────────────────────────────────────────────────────────────────────
//                                       나의 환경
@BindingAdapter("envImage")
fun ImageView.setEnvImage(data: Int) {
    when(data) {
        in 0..20 -> setBackgroundResource(R.drawable.env_image_background_stage1_very_dirty)
        in 21..40 -> setBackgroundResource(R.drawable.env_image_background_stage2_dirty)
        in 41..60 -> setBackgroundResource(R.drawable.env_image_background_stage3_usual)
        in 61..80 -> setBackgroundResource(R.drawable.env_image_background_stage4_clean)
        else -> setBackgroundResource(R.drawable.env_image_background_stage5_very_clean)
    }
}

@BindingAdapter("oggImage")
fun ImageView.setOGGImage(data: Int) {
    when(data) {
        in 0..20 -> setImageResource(R.drawable.login_image_face)
        in 21..40 -> setImageResource(R.drawable.login_image_face)
        in 41..60 -> setImageResource(R.drawable.login_image_face)
        in 61..80 -> setImageResource(R.drawable.login_image_face)
        else -> setImageResource(R.drawable.login_image_face)
    }
}

// ──────────────────────────────────────────────────────────────────────────────────────
//                                       회원 정보
@BindingAdapter("memberFace")
fun ImageView.setMemberImage(data: Float?) {
    when(data) {
        AIMCO2_ONE -> setImageResource(R.drawable.listaim_image_face_1_color)
        AIMCO2_TWO -> setImageResource(R.drawable.listaim_image_face_2_color)
        AIMCO2_THREE -> setImageResource(R.drawable.listaim_image_face_3_color)
        else -> setImageResource(R.drawable.login_image_face)
    }
}

@BindingAdapter("memberAim")
fun TextView.setAimTitle(data: MyCondition?) {
    data?.let {
        text = when(data.aim) {
            AIMCO2_ONE -> "초보"
            AIMCO2_TWO -> "중수"
            AIMCO2_THREE -> "고수"
            else -> "비활동가"
        }
    }
}
