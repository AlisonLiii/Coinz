package com.example.s1891132.coinz.AdapterForListView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.s1891132.coinz.R
import com.example.s1891132.coinz.ClassAndItem.RateItem

class RateAdapter(private val context: Context,
                  public val dataSource: ArrayList<RateItem>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }


    override fun getItem(position: Int): Any {
        return dataSource[position]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.item_rate, parent, false)
        val type = rowView.findViewById<TextView>(R.id.type_item)
        val rate = rowView.findViewById<TextView>(R.id.rate_item)
        val rateItem= getItem(position) as RateItem
        type.text = rateItem.type
        rate.text=rateItem.rate.toString()
        return rowView
    }

}