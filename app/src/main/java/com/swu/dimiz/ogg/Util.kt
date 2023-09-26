package com.swu.dimiz.ogg

import android.annotation.SuppressLint
import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// 인증 등록 날짜를 문자열로 반환하는 메서드
@SuppressLint("SimpleDateFormat")
fun converLongToDateString(systemTime: Long): String {

    return SimpleDateFormat("yyyy-mm-dd' 'HH:mm")
        .format(systemTime).toString()
}

// 활동 등록일이 주어지면 오늘이 활동 며칠 째인지 계산해주는 메서드
private val ONE_DAY_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
fun convertDurationToFormatted(startTimeMilli: Long): Int {

    val currentTimeMilli: Long = System.currentTimeMillis()
    val durationMilli = currentTimeMilli - startTimeMilli

    return when {
        startTimeMilli == 0L -> 0
        durationMilli < ONE_DAY_MILLIS -> {
            1
        }
        else -> {
            val days = TimeUnit.DAYS.convert(durationMilli, TimeUnit.MILLISECONDS)
            days.toInt()
        }
    }
}

// 탄소량에 따라 스티커를 결정해주는 메서드

// 탄소량에 따라 프로그레스바를 계산해주는 메서드