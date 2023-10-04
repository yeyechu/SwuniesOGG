package com.swu.dimiz.ogg.contents.listset.listutils

data class ListData (
    var aId: Int = 0,
    var aNumber: Int = 0
        )

data class NumberData (
    var nId: Int = 0,
    var nNumber: Int = 0
)

data class StampData (
    var sId: Int = 0,
    var sNumber: Float = 0f,
    var today: Int = 0
)

data class TodayData (
    var tId: Int = 0,
    var tNumber: Float = 0f
)

data class BadgeStatusHolder(
    var id: Int,
    var visible: Boolean
)

const val AIMCO2_ONE = 1.4f
const val AIMCO2_TWO = 2.78f
const val AIMCO2_THREE = 5.22f

const val FLOAT_ZERO = 0f
const val INTEGER_ZERO = 0
const val CO2_WHOLE = 21f
const val DATE_WHOLE = 21
const val ID_MODIFIER = 10001

const val ENERGY = "에너지"
const val CONSUME = "소비"
const val TRANSPORT = "이동수단"
const val RECYCLE = "자원순환"
const val TOGETHER = "전체"