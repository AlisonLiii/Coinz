package com.example.s1891132.coinz

data class CoinzUser(val name:String,val id:String, val email:String,val bio:String, val camp:Int) { //AI is 0, Human is 1
    constructor():this("","","","",2)//2 means the user didn't choose any camp


}