package com.example.s1891132.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.example.s1891132.coinz.adapterForListView.CoinAdapter
import com.example.s1891132.coinz.dataClassAndItem.Coin
import kotlinx.android.synthetic.main.activity_share_coinz.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.design.snackbar
import java.util.ArrayList

class ShareCoinzActivity : AppCompatActivity() {

    private lateinit var listView:ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_coinz)

        setSupportActionBar(toolbar_share)
        val actionBar = supportActionBar
        actionBar?.title = "Coinz you collect yourself in your wallet"
        listView=findViewById(R.id.share_coin_list_view)
        no_coinz_share.visibility= View.INVISIBLE
        contentView?.snackbar("Click on the coin to share with your friend!")
       //get Recipent ID from intent
        val intent=getIntent()
        val otherUserID=intent.getStringExtra("RecipentID")
        Log.i("recipent",otherUserID)

        //list the coin from the coins the user collects himself
        val coinList: ArrayList<Coin> = ArrayList()
        FirestoreUtil.coinSelfCollectListRef.get()
                .addOnSuccessListener {result->
                    if(result.isEmpty)
                    {
                        no_coinz_share.visibility= View.VISIBLE//tell user he has no spare coinz to share
                    }
                    else{
                        for (document in result)
                        {
                            val coin=document.toObject(Coin::class.java)
                            coinList.add(coin)
                        }

                        val adapter= CoinAdapter(this, coinList)//ListView adapter
                        listView.adapter = adapter
                        listView.onItemClickListener = AdapterView.OnItemClickListener{ _, view, position, _ ->
                            val coin=coinList[position]
                            FirestoreUtil.shareCoinz(view,otherUserID,coin)//Share the certain coin user selects to his friends
                            view.snackbar("sent!")
                            adapter.remove(position)//remove the coin which has been sent from the list
                        }

                    }
            }

    }
}
