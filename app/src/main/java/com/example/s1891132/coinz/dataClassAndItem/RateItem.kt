package com.example.s1891132.coinz.dataClassAndItem


data class RateItem(val type: String,val rate:Double) {
    constructor():this("",0.0)
    //type is the current type, rate means the exchange rate from current to gold
}