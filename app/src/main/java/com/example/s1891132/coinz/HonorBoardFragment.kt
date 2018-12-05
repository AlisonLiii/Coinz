package com.example.s1891132.coinz


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class HonorBoardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_honor_board, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()

        FirestoreUtil.aiGoldRef.get().addOnSuccessListener {document->
            val aiGold=document["accountGold"] as Double
        }
        FirestoreUtil.humanGoldRef.get().addOnSuccessListener {document->
            val humanGold=document["accountGold"] as Double
        }

        FirestoreUtil.individualGoldRef.get().addOnSuccessListener {document->
            val individualGold=document["accountGold"] as Double
        }
    }


}
