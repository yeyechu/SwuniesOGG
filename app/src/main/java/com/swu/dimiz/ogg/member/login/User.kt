package com.swu.dimiz.ogg.member.login

class User {
    constructor(){

    }
    constructor(email:String, password:String, nickname:String){
        this.email=email
        this.password=password
        this.nickname=nickname
    }

    var email:String=""
    var password:String=""
    var nickname:String=""
    var posting: Int = 0
}