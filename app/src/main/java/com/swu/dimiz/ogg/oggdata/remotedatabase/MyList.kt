package com.swu.dimiz.ogg.oggdata.remotedatabase

import androidx.room.PrimaryKey

/*
fireStore 위치
User/사용자 이메일/1(Profject1~..)/index

활동 계획 + 활동 탭
 */

//@Entity(tableName = "list_set")
data class MyList (  //21일간 활동 계획
    var day1act: Int?= 0,
    var day2act: Int?= 0,
    var day3act: Int?= 0,
    var day4act: Int?= 0,
    var day5act: Int?= 0,
    var day6act: Int?= 0,
    var day7act: Int?= 0,
    var day8act: Int?= 0,
    var day9act: Int?= 0,
    var day10act: Int?= 0,
    var day11act: Int?= 0,
    var day12act: Int?= 0,
    var day13act: Int?= 0,
    var day14act: Int?= 0,
    var day15act: Int?= 0,
    var day16act: Int?= 0,
    var day17act: Int?= 0,
    var day18act: Int?= 0,
    var day19act: Int?= 0,
    var day20act: Int?= 0,
    var day21act: Int?= 0
){
    fun setFirstList(actId:Int){
        day1act = actId
        day2act= actId
        day3act= actId
        day4act= actId
        day5act= actId
        day6act= actId
        day7act= actId
        day8act= actId
        day9act= actId
        day10act= actId
        day11act= actId
        day12act= actId
        day13act= actId
        day14act= actId
        day15act= actId
        day16act= actId
        day17act= actId
        day18act= actId
        day19act= actId
        day20act= actId
        day21act= actId
    }


}




