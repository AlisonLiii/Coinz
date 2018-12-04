package com.example.s1891132.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import com.example.s1891132.coinz.AdapterForListView.CoinAdapter
import com.example.s1891132.coinz.ClassAndItem.Coin
import kotlinx.android.synthetic.main.activity_share_coinz.*
import org.jetbrains.anko.design.snackbar
import java.util.ArrayList

class ShareCoinzActivity : AppCompatActivity() {

    private lateinit var listView:ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_coinz)
        setSupportActionBar(toolbar_share)
        val actionBar = supportActionBar
        actionBar?.title = "Coinz you have in wallet"
        listView=findViewById(R.id.share_coin_list_view)
        val intent=getIntent()
        val otherUserID=intent.getStringExtra("RecipentID")
        Log.i("recipent",otherUserID)
        var coinSelfCollectList: ArrayList<Coin> = ArrayList()
        FirestoreUtil.coinListRef.get()
                .addOnSuccessListener {result->
                    for (document in result)
                    {
                        val coin=document.toObject(Coin::class.java)
                        coinSelfCollectList.add(coin)
                    }
                    val adapter= CoinAdapter(this, coinSelfCollectList)
                    listView.adapter = adapter
                    listView.onItemClickListener = AdapterView.OnItemClickListener{ adapterView, view, position, id->
                        val coin=coinSelfCollectList[position]
                        FirestoreUtil.shareCoinz(view,otherUserID,coin)
                        snackbar(view,"sent!")
                        adapter.remove(position)
                    }
                }

    }
}
