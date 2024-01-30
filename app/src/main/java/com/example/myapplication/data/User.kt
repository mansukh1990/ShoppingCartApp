package com.example.myapplication.data

data class User(
    val firstname : String,
    val lastName : String,
    val email : String,
    val emails : String,


    val imagePath : String = ""

){
    constructor():this("","" ,"","")
}
