package com.example.s1891132.coinz

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.example.s1891132.coinz.adapterForListView.CoinAdapter
import com.example.s1891132.coinz.adapterForListView.RateAdapter
import com.example.s1891132.coinz.dataClassAndItem.Coin
import com.example.s1891132.coinz.dataClassAndItem.RateItem
import kotlinx.android.synthetic.main.activity_bank_coinz.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.util.ArrayList

class BankCoinzActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var settings: SharedPreferences
    private lateinit var coinInfo:String

    private var coinzFile="CoinzGeoInfoToday"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {

            R.id.coin_I_collect -> {
                //show the list of coinz that the user collect himself

                hint_bank.visibility=View.INVISIBLE
                listView.visibility=View.INVISIBLE
                hint_no_coin_bank.visibility=View.INVISIBLE
                val coinSelfCollectList:ArrayList<Coin> = ArrayList()

                FirestoreUtil.coinSelfCollectListRef.get()
                        .addOnSuccessListener {result->
                            if(result.isEmpty)
                                hint_no_coin_bank.visibility=View.VISIBLE
                                //show hint: no coins left
                            else{
                                for (document in result)
                                {
                                    val coin=document.toObject(Coin::class.java)
                                    coinSelfCollectList.add(coin)
                                }
                                val adapter= CoinAdapter(this, coinSelfCollectList)
                                listView.adapter = adapter
                                listView.visibility=View.VISIBLE
                                listView.onItemClickListener = AdapterView.OnItemClickListener{adapterView, view, position, id->
                                    FirestoreUtil.currentUserDocRef.get().addOnSuccessListener { document->
                                        if(document!=null)
                                        {
                                            val originBankNum=document["bankNum"] as Double
                                            if(originBankNum>=25.0)
                                                view.snackbar("Oops! You have banked 25 coins into account today. Why not exchange the rest with your friends?","OK")
                                                {}
                                            else{
                                                //update bank account balance
                                                val coin=coinSelfCollectList[position]
                                                updateBalance(coin.type,coin.value)
                                                FirestoreUtil.updateBankNumToday()
                                                FirestoreUtil.deleteCoinInList(FirestoreUtil.coinSelfCollectListRef,coin.id)
                                                adapter.remove(position)//remove the coin in the listview
                                            }

                                        }
                                    }
                                }
                            }
                        }
                return@OnNavigationItemSelectedListener true
            }

        //show the list of coinz from friends
            R.id.coin_from_friend -> {
                hint_bank.visibility=View.INVISIBLE
                listView.visibility=View.INVISIBLE
                hint_no_coin_bank.visibility=View.INVISIBLE

                val coinFromFreindsList:ArrayList<Coin> = ArrayList()
                FirestoreUtil.coinFromOthersRef.get()
                        .addOnSuccessListener {result->
                            if(result.isEmpty)
                                hint_no_coin_bank.visibility=View.VISIBLE

                            else{
                                for (document in result)
                                {
                                    val coin=document.toObject(Coin::class.java)
                                    coinFromFreindsList.add(coin)
                                }
                                val adapter= CoinAdapter(this, coinFromFreindsList)
                                listView.adapter = adapter
                                listView.visibility=View.VISIBLE
                                listView.onItemClickListener = AdapterView.OnItemClickListener{adapterView, view, position, id->
                                    val coin=coinFromFreindsList[position]
                                    updateBalance(coin.type,coin.value)
                                    FirestoreUtil.deleteCoinInList(FirestoreUtil.coinFromOthersRef,coin.id)
                                    adapter.remove(position)
                                }
                            }
                        }

                return@OnNavigationItemSelectedListener true
            }
            //show the four types of currency and their rates and user can exchange them into gold
            R.id.exchange_gold -> {
                hint_bank.visibility=View.INVISIBLE
                listView.visibility=View.VISIBLE
                hint_no_coin_bank.visibility=View.INVISIBLE

                //parse GeoJson information in the shared preference file to get the rate
                val jsonObject= JSONObject(coinInfo)
                val jsonRate=jsonObject.getJSONObject("rates")
                val dolrRate= RateItem("DOLR", jsonRate.getString("DOLR").toDouble())
                val quidRate= RateItem("QUID", jsonRate.getString("QUID").toDouble())
                val penyRate= RateItem("PENY", jsonRate.getString("PENY").toDouble())
                val shilRate= RateItem("SHIL", jsonRate.getString("SHIL").toDouble())

                val rateList= arrayListOf(shilRate,dolrRate,quidRate,penyRate)

                val adapter= RateAdapter(this, rateList)
                listView.adapter = adapter

                listView.onItemClickListener=AdapterView.OnItemClickListener{adapterView, view, position, id->

                    val rateItem=rateList[position]
                    FirestoreUtil.getCurrentUser {
                        FirestoreUtil.convertToGold(contentView,rateItem.type,rateItem.rate,it.name)
                    }
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_coinz)

        setSupportActionBar(toolbar_bank)
        val actionBar = supportActionBar
        actionBar?.title = "Bank"

        listView=findViewById(R.id.coin_list_view)
        hint_no_coin_bank.visibility=View.INVISIBLE

        //get GeoJson information of the day
        settings=getSharedPreferences(coinzFile, Context.MODE_PRIVATE)
        coinInfo=settings.getString(currentDate(),"")

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


    }


    override fun onCreateOptionsMenu(menu: Menu):Boolean{
        menuInflater.inflate(R.menu.toolbar_in_bank,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        startActivity<MainActivity>()
        return true
    }


    private fun updateBalance(type:String,value:Double){
        if(type.equals("PENY",true))
        {
            FirestoreUtil.updateAccountBalance(value,0.0,0.0,0.0,1)
            FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,value,0.0,0.0,0.0,-1)
        }
        else if(type.equals("DOLR",true))
        {
            FirestoreUtil.updateAccountBalance(0.0,value,0.0,0.0,1)
            FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,0.0,value,0.0,0.0,-1)
        }
        else if(type.equals("SHIL",true))
        {
            FirestoreUtil.updateAccountBalance(0.0,0.0,value,0.0,1)
            FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,0.0,0.0,value,0.0,-1)
        }
        else if(type.equals("QUID",true))
        {
            FirestoreUtil.updateAccountBalance(0.0,0.0,0.0,value,1)
            FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,0.0,0.0,0.0,value,-1)
        }

        else
            Log.d("coinz","Invalid opeartion")
    }

}

