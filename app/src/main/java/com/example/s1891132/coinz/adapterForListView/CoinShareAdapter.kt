package com.example.s1891132.coinz.adapterForListView

import android.annotation.SuppressLint
import android.widget.BaseAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.s1891132.coinz.R
import com.example.s1891132.coinz.dataClassAndItem.Coin

class CoinShareAdapter(context: Context,private val dataSource: ArrayList<Coin>) : BaseAdapter() {

    //layout inflater
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    //get the item's position in the list
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }



    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.coinz_share_list_view, parent, false)
        val curtypeTextView = rowView.findViewById(R.id.cur_type) as TextView
        val curvalueTextView = rowView.findViewById(R.id.cur_value) as TextView
        val coin = getItem(position) as Coin
        curtypeTextView.text = coin.type
        curvalueTextView.text=coin.value.toString()
        return rowView
    }

    //remove the coin after banking into its account/sharing it to user's friend
    fun remove(postion:Int)
    {
        dataSource.removeAt(postion)
        notifyDataSetChanged()
    }

}