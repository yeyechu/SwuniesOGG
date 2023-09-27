package com.swu.dimiz.ogg.oggdata.remotedatabase

import java.sql.Date

/*
fireStore 위치
User/사용자 이메일/1(Profject1~..)/Graph
 */

data class MyGraph (
    var startDate: Date ?=null,

    var postCount : Int = 0,
    var post1 : Int = 0,
    var post2 : Int = 0,
    var post3 : Int = 0,

    var reactionCount : Int = 0,
    var reaction1 : Int = 0,
    var reaction2 : Int = 0,
    var reaction3 : Int = 0,

    var co2Count : Int = 0,
    var co21 : Int = 0,
    var co22 : Int = 0,
    var co23 : Int = 0,

    var stampCount : Int = 0,
    var stamp1 : Int = 0,
    var stamp2 : Int = 0,
    var stamp3 : Int = 0,
    )