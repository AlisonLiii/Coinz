package com.example.s1891132.coinz.ClassAndItem

import android.content.Context
import kotlinx.android.synthetic.main.item_rate.*

data class RateItem(val type: String,val rate:Double) {
    constructor():this("",0.0)
}