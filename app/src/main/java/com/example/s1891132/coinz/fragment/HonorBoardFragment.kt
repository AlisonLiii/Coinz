package com.example.s1891132.coinz.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.s1891132.coinz.FirestoreUtil
import com.example.s1891132.coinz.R
import kotlinx.android.synthetic.main.fragment_honor_board.*

class HonorBoardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_honor_board, container, false)
    }

    override fun onStart() {
        super.onStart()
        //If the first user of the app check the honor board, it will show not initialize on the gold value,
        //because no one has any gold at the moment

        FirestoreUtil.aiGoldRef.get().addOnSuccessListener { document->
            if(document.exists())
            {
                val aiGold=document["gold"] as Double
                honor_ai.text=aiGold.toString()
            }
        }
        FirestoreUtil.humanGoldRef.get().addOnSuccessListener { document->
            if(document.exists())
            {
                val humanGold=document["gold"] as Double
                honor_human.text=humanGold.toString()
            }

        }

        FirestoreUtil.individualGoldRef.get().addOnSuccessListener { document->
            if(document.exists())
            {
                val individualGold=document["gold"] as Double
                val individualName=document["email"]
                gold_indi_honor.text=individualGold.toString()
                user_name_honor.text=individualName.toString()
            }
        }
    }


}
