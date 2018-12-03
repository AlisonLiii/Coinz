package com.example.s1891132.coinz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class RateAdapter(private val context: Context,
                  public val dataSource: ArrayList<Double>) : BaseAdapter() {

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
        val rateValue = getItem(position) as Double
        val typeList= arrayListOf<String>("SHIL","DOLR","QUID","PENY")
        type.text = typeList.get(position)
        rate.text=rateValue.toString()
        return rowView
    }

}