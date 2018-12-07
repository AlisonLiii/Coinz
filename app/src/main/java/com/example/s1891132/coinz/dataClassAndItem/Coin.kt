package com.example.s1891132.coinz.dataClassAndItem

import com.mapbox.mapboxsdk.geometry.LatLng


data class Coin(val id:String,val type:String, val value:Double,val latlng:LatLng){
    constructor():this("","",0.0, LatLng(0.0,0.0))
}
//contains the id, currency type, value, and latlng position of the coin