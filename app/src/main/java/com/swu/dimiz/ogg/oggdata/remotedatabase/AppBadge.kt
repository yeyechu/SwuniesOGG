package com.swu.dimiz.ogg.oggdata.remotedatabase

/*
fireStore 위치
기본 정보 이미 추가되어있음
App/Badge/
 */

data class AppBadge ( //전체 뱃지 정보
    var BadgeId: Int = 0,             //전체 뱃지 ID

    var BadgeTitile: String = "",     //뱃지 제목

    var BadgeCode: String = "",       //뱃지 코드

    var BadgeRequire: String = ""     //획득 조건
)