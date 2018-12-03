package com.example.s1891132.coinz

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import android.content.Context
import kotlinx.android.synthetic.main.item_rate.*

class RateItem(val type: String,val rate:Double,
                 private val context: Context)
    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.type_item.text = type
        viewHolder.rate_item.text = rate.toString()
    }

    override fun getLayout() = R.layout.item_rate
}