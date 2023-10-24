package com.swu.dimiz.ogg

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

// 인증 등록 날짜를 문자열로 반환하는 메서드
@SuppressLint("SimpleDateFormat")
fun converLongToDateString(systemTime: Long): String {

    return SimpleDateFormat("yyyy년 MM월 dd일")
        .format(systemTime).toString()
}

// 활동 등록일이 주어지면 오늘이 활동 며칠 째인지 계산해주는 메서드
fun convertDurationToFormatted(startTimeMilli: Long): Int {

    val currentTimeMilli: Long = System.currentTimeMillis()
    val durationMilli = currentTimeMilli - startTimeMilli

    return if (startTimeMilli == 0L) 0
    else {
        val days = TimeUnit.DAYS.convert(durationMilli, TimeUnit.MILLISECONDS)
        (days + 1).toInt()
    }
}

fun convertDurationToInt(startTime: Long): Int {
    val current = java.text.SimpleDateFormat("yyyyMMddHHmmss").format(Date()).toLong()
    val duration = (current - startTime)/1000_000

    return duration.toInt() + 1 //당일에 시작했어도 1일로 표시됨
}

fun convertToDP(context: Context, value: Int): Int {
    return (value * context.resources.displayMetrics.density).roundToInt()
}

