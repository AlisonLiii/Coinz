package com.example.s1891132.coinz.Fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.s1891132.coinz.BankCoinzActivity
import com.example.s1891132.coinz.ClassAndItem.Coin
import com.example.s1891132.coinz.ClassAndItem.CoinzUser
import com.example.s1891132.coinz.FirestoreUtil

import com.example.s1891132.coinz.R
import com.example.s1891132.coinz.ShareCoinzActivity
import com.example.s1891132.coinz.message.AppConstants
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_my_property.*
import kotlinx.android.synthetic.main.fragment_search_friend.*
import kotlinx.android.synthetic.main.fragment_search_friend.view.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.toast

class SearchFriendFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_friend, container, false)
        view.apply {

            fab_search_id.setOnClickListener {

                if (editText_id.text.isNullOrBlank()) {
                    snackbar("Enter email please")
                    return@setOnClickListener
                }
                var id = "nouserwiththisemail"
                FirestoreUtil.firestoreInstance.collection("users").whereEqualTo("email", editText_id.text.toString())
                        .get().addOnSuccessListener {
                            if(it.isEmpty)
                                snackbar("No such user")
                            else {
                                it.forEach {
                                    id = it["id"] as String
                                    FirestoreUtil.getCurrentUser { CoinzUser->
                                        if(CoinzUser.id!=id)
                                        {
                                            val intent=Intent(context, ShareCoinzActivity::class.java)
                                            intent.putExtra("RecipentID",id)
                                            context?.startActivity(intent)
                                        }
                                        else snackbar("you cannot search yourself")
                                    }
                                }
                            }
                        }
            }
        }
        return view
    }



}
