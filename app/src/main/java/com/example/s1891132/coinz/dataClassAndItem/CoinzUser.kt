package com.example.s1891132.coinz.dataClassAndItem

import com.example.s1891132.coinz.currentDate

data class CoinzUser(val name:String,val id:String, val email:String,val bio:String, val camp:Double,
                     val walletPeny:Double,val walletDolr:Double,val walletShil:Double, val walletQuid:Double,val accountGold:Double,
                     val accountPeny:Double,val accountDolr:Double,val accountShil:Double, val accountQuid:Double,val bankNum:Double,
                     val date:String,val walkingDistance:Double ) { //AI is 0, Human is 1
    constructor():this("","","","",2.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0, currentDate(),0.0)//2 means the user didn't choose any camp
    /*although the constructor is never used and give us a warning in code analysis,
     without constructor, the app will crash for the reason that the class does not define a no-argument constructor
      */

    //bankNum here means how many coins the user collect himself has been banked into the bank account.
    //date here records the date of the day
}