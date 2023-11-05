package com.swu.dimiz.ogg.oggdata.remotedatabase

/*
fireStore 위치
User/사용자 이메일/Reaction/id
 */

data class MyReaction(
    var feedId : String ="",

    var reactionLike: Boolean = false,
    var reactionFun: Boolean = false,
    var reactionGreat: Boolean = false
)
