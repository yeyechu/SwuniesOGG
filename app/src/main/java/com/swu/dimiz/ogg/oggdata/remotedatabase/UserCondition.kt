package com.swu.dimiz.ogg.oggdata.remotedatabase

import java.sql.Date

data class UserCondition(var email:String="",
                         var password:String="",
                         var nickname:String="",
                         var posting: Int = 0,
                         var report: Int = 0,
                         var car: Int = 0,
                         var level: Int = 0,
                         var startDate: Date?=null)
