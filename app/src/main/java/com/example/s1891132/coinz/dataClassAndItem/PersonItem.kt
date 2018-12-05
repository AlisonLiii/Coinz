package com.example.s1891132.coinz.dataClassAndItem

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import android.content.Context
import com.example.s1891132.coinz.R
import kotlinx.android.synthetic.main.item_person.*

//this class follows the tutorial below
//https://www.youtube.com/watch?v=a9I7Ppzh1_Y

class PersonItem(val person: CoinzUser,
                 private val context: Context)
    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.person_name_item.text = person.name
        viewHolder.person_bio_item.text = person.bio
        if(person.camp==0.0)//camp=0.0->the user belongs to AI, camp=1.0->the user belongs to human
            viewHolder.person_camp_item.text=context.getString(R.string.camp_is_AI)
        else
            viewHolder.person_camp_item.text=context.getString(R.string.camp_is_human)
    }

    override fun getLayout() = R.layout.item_person
}