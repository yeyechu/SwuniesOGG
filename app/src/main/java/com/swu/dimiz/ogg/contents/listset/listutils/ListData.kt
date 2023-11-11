package com.swu.dimiz.ogg.contents.listset.listutils

data class ListData (
    var aId: Int = 0,
    var aNumber: Int = 0
        )

// when(today) {
//     0 -> 아직 안 지난 날짜
//     1 -> 오늘
//     2 -> 이미 지난 날짜
data class StampData (
    var sId: Int = 0,
    var sNumber: Float = 0f,
    var today: Int = 0
)

data class BadgeLocation (
    var bId: Int = 0,
    var bx: Float = 0f,
    var by: Float = 0f
)

data class FeedReact(
    var id: String,
    var reactionSum: Int,
    var title : String
    )

data class PostCount(
    var id: Int,
    var count: Int
)

data class Checklist (
    var checkBoxTitle: String,
    var checkBoxSubtitle: String
)

const val AIMCO2_ONE = 1.4f
const val AIMCO2_TWO = 2.78f
const val AIMCO2_THREE = 5.22f

const val FLOAT_ZERO = 0f
const val INTEGER_ZERO = 0
const val CO2_WHOLE = 21f
const val DATE_WHOLE = 21
const val ID_MODIFIER = 10000
const val LIST_SIZE = 5

const val BADGE_SIZE = 350

const val ENERGY = "에너지"
const val CONSUME = "소비"
const val TRANSPORT = "이동수단"
const val RECYCLE = "자원순환"
const val TOGETHER = "전체"

const val NO_TITLE = "제목없음"

const val GRAPH_OBJECT = "graph"