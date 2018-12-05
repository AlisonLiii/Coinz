package com.example.s1891132.coinz.dataClassAndItem

import com.mapbox.mapboxsdk.geometry.LatLng

data class CoinFromOthers(val id:String,val type:String, val value:Double,val latlng: LatLng,val num:Double){
    constructor():this("","",0.0, LatLng(0.0,0.0),0.0)
}