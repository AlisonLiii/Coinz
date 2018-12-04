package com.example.s1891132.coinz.AdapterForListView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import android.widget.TextView
import com.example.s1891132.coinz.ClassAndItem.Coin
import com.example.s1891132.coinz.R

//https://www.raywenderlich.com/155-android-listview-tutorial-with-kotlin

class CoinAdapter(private val context: Context,
 public val dataSource: ArrayList<Coin>) : BaseAdapter() {

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
        val rowView = inflater.inflate(R.layout.coinz_list_view, parent, false)
        val curtypeTextView = rowView.findViewById(R.id.cur_type) as TextView
        val curvalueTextView = rowView.findViewById(R.id.cur_value) as TextView
        //val bankBtn=rowView.findViewById<Button>(R.id.bank_in_btn)
        val coin = getItem(position) as Coin
        curtypeTextView.text = coin.type
        curvalueTextView.text=coin.value.toString()
        /*bankBtn.setOnClickListener{
            dataSource.removeAt(position)
            notifyDataSetChanged()
        }*/
        return rowView
    }

    fun remove(postion:Int)
    {
        dataSource.removeAt(postion)
        notifyDataSetChanged()
    }

}