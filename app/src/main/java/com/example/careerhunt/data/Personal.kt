package com.example.careerhunt.data

data class Personal(
    var personalID:Int=0,
    var name:String ="",
    var email:String ="",
    var password:String ="",
    var graduatedFrom:String ="",
    var profile_img: ByteArray,
    var phoneNum:String ="",
    var gender:String ="",
    var jobField:String =""
)



