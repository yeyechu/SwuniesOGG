package com.swu.dimiz.ogg.oggdata.remotedatabase

import java.net.URI
import java.sql.Date

/*
fireStore 위치
User/사용자 이메일/Profject1~../Graph
 */

data class MyGraph (    //통계 페이지 들어올때마다 업데이트
    var startDate: Date ?=null,

    //카테고리 별 줄인 탄소량
    var energy : Int = 0,
    var consumption : Int = 0,
    var resource : Int = 0,
    var transport : Int = 0,

    //var co2Count : Int = 0,
    //가장 많은 탄소를 줄인 활동명
    var co21 : Int = 0,
    var co22 : Int = 0,
    var co23 : Int = 0,

    //var reactionCount : Int = 0,
    //가장 많은 반응을 받은 활동
    var reactionName:String = "",
    var reactionURI: URI ?=null,
    var good : Int = 0,
    var funny : Int = 0,
    var best : Int = 0,

    //var postCount : Int = 0,
    //가장 많이 인증한 활동명
    var post1 : Int = 0,
    var post2 : Int = 0,
    var post3 : Int = 0,

    //특별활동 전체 순위
    var extraRank:Float = 0f
    )