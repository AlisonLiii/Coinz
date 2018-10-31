package com.example.s1891132.coinz

import android.util.Log
import java.util.*

fun currentDate():String {
    val calendar= Calendar.getInstance()
    return calendar.get(Calendar.YEAR).toString()+"/"+(calendar.get(Calendar.MONTH)+1).toString()+"/"+calendar.get(Calendar.DAY_OF_MONTH).toString()
}
fun currentUrl():String{
    return "http://homepages.inf.ed.ac.uk/stg/coinz/"+ currentDate()+"/coinzmap.geojson"
}