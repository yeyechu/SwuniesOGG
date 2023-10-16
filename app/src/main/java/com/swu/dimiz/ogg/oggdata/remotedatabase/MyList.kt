package com.swu.dimiz.ogg.oggdata.remotedatabase

import androidx.room.PrimaryKey
import com.swu.dimiz.ogg.contents.listset.listutils.ListData

/*
fireStore 위치
User/사용자 이메일/Profject1~../index

활동 계획 + 활동 탭
 */

//@Entity(tableName = "list_set")
data class MyList (  //21일간 활동 계획
    var day1act: ListData = ListData(0, 0),
    var day2act: ListData = ListData(0, 0),
    var day3act: ListData = ListData(0, 0),
    var day4act: ListData = ListData(0, 0),
    var day5act: ListData = ListData(0, 0),
    var day6act: ListData = ListData(0, 0),
    var day7act: ListData = ListData(0, 0),
    var day8act: ListData = ListData(0, 0),
    var day9act: ListData = ListData(0, 0),
    var day10act: ListData = ListData(0, 0),
    var day11act: ListData = ListData(0, 0),
    var day12act: ListData = ListData(0, 0),
    var day13act: ListData = ListData(0, 0),
    var day14act: ListData = ListData(0, 0),
    var day15act: ListData = ListData(0, 0),
    var day16act: ListData = ListData(0, 0),
    var day17act: ListData = ListData(0, 0),
    var day18act: ListData = ListData(0, 0),
    var day19act: ListData = ListData(0, 0),
    var day20act: ListData = ListData(0, 0),
    var day21act: ListData = ListData(0, 0)
){
    fun setFirstList(actId:Int, aimDo :Int){
        day1act = ListData(actId, aimDo)
        day2act= ListData(actId, aimDo)
        day3act= ListData(actId, aimDo)
        day4act= ListData(actId, aimDo)
        day5act= ListData(actId, aimDo)
        day6act= ListData(actId, aimDo)
        day7act= ListData(actId, aimDo)
        day8act= ListData(actId, aimDo)
        day9act= ListData(actId, aimDo)
        day10act= ListData(actId, aimDo)
        day11act= ListData(actId, aimDo)
        day12act= ListData(actId, aimDo)
        day13act= ListData(actId, aimDo)
        day14act= ListData(actId, aimDo)
        day15act= ListData(actId, aimDo)
        day16act= ListData(actId, aimDo)
        day17act= ListData(actId, aimDo)
        day18act= ListData(actId, aimDo)
        day19act= ListData(actId, aimDo)
        day20act= ListData(actId, aimDo)
        day21act= ListData(actId, aimDo)
    }

    fun setLeftdayList(today:Int, actId:Int, aimDo :Int){
        when(today){
            1->{
                day1act = ListData(actId, aimDo)
                day2act= ListData(actId, aimDo)
                day3act= ListData(actId, aimDo)
                day4act= ListData(actId, aimDo)
                day5act= ListData(actId, aimDo)
                day6act= ListData(actId, aimDo)
                day7act= ListData(actId, aimDo)
                day8act= ListData(actId, aimDo)
                day9act= ListData(actId, aimDo)
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            2->{
                day2act= ListData(actId, aimDo)
                day3act= ListData(actId, aimDo)
                day4act= ListData(actId, aimDo)
                day5act= ListData(actId, aimDo)
                day6act= ListData(actId, aimDo)
                day7act= ListData(actId, aimDo)
                day8act= ListData(actId, aimDo)
                day9act= ListData(actId, aimDo)
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            3->{
                day3act= ListData(actId, aimDo)
                day4act= ListData(actId, aimDo)
                day5act= ListData(actId, aimDo)
                day6act= ListData(actId, aimDo)
                day7act= ListData(actId, aimDo)
                day8act= ListData(actId, aimDo)
                day9act= ListData(actId, aimDo)
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            4->{
                day4act= ListData(actId, aimDo)
                day5act= ListData(actId, aimDo)
                day6act= ListData(actId, aimDo)
                day7act= ListData(actId, aimDo)
                day8act= ListData(actId, aimDo)
                day9act= ListData(actId, aimDo)
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            5->{
                day5act= ListData(actId, aimDo)
                day6act= ListData(actId, aimDo)
                day7act= ListData(actId, aimDo)
                day8act= ListData(actId, aimDo)
                day9act= ListData(actId, aimDo)
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            6->{
                day6act= ListData(actId, aimDo)
                day7act= ListData(actId, aimDo)
                day8act= ListData(actId, aimDo)
                day9act= ListData(actId, aimDo)
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            7->{
                day7act= ListData(actId, aimDo)
                day8act= ListData(actId, aimDo)
                day9act= ListData(actId, aimDo)
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            8->{
                day8act= ListData(actId, aimDo)
                day9act= ListData(actId, aimDo)
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            9->{
                day9act= ListData(actId, aimDo)
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            10->{
                day10act= ListData(actId, aimDo)
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            11->{
                day11act= ListData(actId, aimDo)
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            12->{
                day12act= ListData(actId, aimDo)
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            13->{
                day13act= ListData(actId, aimDo)
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            14->{
                day14act= ListData(actId, aimDo)
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            15->{
                day15act= ListData(actId, aimDo)
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            16->{
                day16act= ListData(actId, aimDo)
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            17->{
                day17act= ListData(actId, aimDo)
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            18->{
                day18act= ListData(actId, aimDo)
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            19->{
                day19act= ListData(actId, aimDo)
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            20->{
                day20act= ListData(actId, aimDo)
                day21act= ListData(actId, aimDo)
            }
            21->{
                day21act= ListData(actId, aimDo)
            }
        }
    }
}




