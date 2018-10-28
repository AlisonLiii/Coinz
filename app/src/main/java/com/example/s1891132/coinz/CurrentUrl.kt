package com.example.s1891132.coinz

import android.util.Log
import java.util.*

fun CurrentUrl():String{//object or class here
    val calendar= Calendar.getInstance()
    val year=calendar.get(Calendar.YEAR).toString()
    val month=(calendar.get(Calendar.MONTH)+1).toString()
    val day=calendar.get(Calendar.DAY_OF_MONTH).toString()
    val front="http://homepages.inf.ed.ac.uk/stg/coinz/"
    val end= "/coinzmap.geojson"
    val currentUrl=front+year+"/"+month+"/"+day+end
    Log.i("currentUrl",currentUrl)
    return currentUrl
}