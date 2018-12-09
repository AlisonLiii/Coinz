package com.example.s1891132.coinz

import android.support.annotation.VisibleForTesting
import java.util.*

@VisibleForTesting
fun currentDate():String {
    val calendar=Calendar.getInstance()
    val year=calendar.get(Calendar.YEAR).toString()
    var month=(calendar.get(Calendar.MONTH)+1).toString()
    var day=calendar.get(Calendar.DAY_OF_MONTH).toString()
    if(month.toInt()<10)
        month= "0$month"
    if(day.toInt()<10)
        day= "0$day"
    return "$year/$month/$day"
}
fun currentUrl():String{
    return "http://homepages.inf.ed.ac.uk/stg/coinz/"+ currentDate()+"/coinzmap.geojson"
}