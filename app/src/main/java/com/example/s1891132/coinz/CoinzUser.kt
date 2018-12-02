package com.example.s1891132.coinz

data class CoinzUser(val name:String,val id:String, val email:String,val bio:String, val camp:Int,
                     val walletPeny:Double,val walletDolr:Double,val walletShil:Double, val walletQuid:Double,val accountGold:Double,
                     val accountPeny:Double,val accountDolr:Double,val accountShil:Double, val accountQuid:Double,val bankNum:Double,val date:String ) { //AI is 0, Human is 1
    constructor():this("","","","",2,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0, currentDate())//2 means the user didn't choose any camp

}