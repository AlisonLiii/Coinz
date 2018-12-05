package com.example.s1891132.coinz.dataClassAndItem

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import android.content.Context
import com.example.s1891132.coinz.R
import kotlinx.android.synthetic.main.item_person.*

class PersonItem(val person: CoinzUser,
                 private val context: Context)
    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.person_name_item.text = person.name
        viewHolder.person_bio_item.text = person.bio
        if(person.camp==0.0)
            viewHolder.person_camp_item.text="camp:\n"+"AI"
        else
            viewHolder.person_camp_item.text="camp:\n"+"Human"
    }

    override fun getLayout() = R.layout.item_person
}