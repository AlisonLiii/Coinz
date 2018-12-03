package com.example.s1891132.coinz

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_bank_coinz.*
import org.jetbrains.anko.longToast
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
            R.id.put_into_account -> {
                hint_bank.visibility=View.INVISIBLE
                listView.visibility=View.VISIBLE
                var coinSelfCollectList:ArrayList<Coin> = ArrayList()

                FirestoreUtil.coinListRef.get()
                        .addOnSuccessListener {result->
                            for (document in result)
                            {
                                val coin=document.toObject(Coin::class.java)
                                coinSelfCollectList.add(coin)
                                Log.i("testcoin",coin.type)
                               // Log.i("testcoin",coin.id)
                            }
                            val adapter=CoinAdapter(this,coinSelfCollectList)
                            listView.adapter = adapter

                            listView.onItemClickListener = AdapterView.OnItemClickListener{adapterView, view, position, id->
                                val coin=coinSelfCollectList[position]
                                if(coin.type.equals("PENY",true))
                                    FirestoreUtil.updateAccountBalance(coin.value,0.0,0.0,0.0,1)
                                else if(coin.type.equals("DOLR",true))
                                    FirestoreUtil.updateAccountBalance(0.0,coin.value,0.0,0.0,1)
                                else if(coin.type.equals("SHIL",true))
                                    FirestoreUtil.updateAccountBalance(0.0,0.0,coin.value,0.0,1)
                                else if(coin.type.equals("QUID",true))
                                    FirestoreUtil.updateAccountBalance(0.0,0.0,0.0,coin.value,1)
                                else
                                    Log.d("coinz","Invalid opeartion")
                                FirestoreUtil.updateBankNumToday()
                                FirestoreUtil.deleteCoinInList(FirestoreUtil.coinListRef,coin.id)

                                adapter.remove(position)
                            }
                        }



                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {//TODO:BANK COINZ that friend share

                hint_bank.visibility=View.INVISIBLE
                listView.visibility=View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {//TODO: CONVERT coinz into gold
                //listView.visibility=View.INVISIBLE
                //replaceFragment(ConvertGoldFragment())
                hint_bank.visibility=View.INVISIBLE
                val jsonObject= JSONObject(coinInfo)
                val jsonRate=jsonObject.getJSONObject("rates")
                val shilRate=jsonRate.getString("SHIL") .toDouble()
                val dolrRate=jsonRate.getString("DOLR").toDouble()
                val quidRate=jsonRate.getString("QUID").toDouble()
                val penyRate=jsonRate.getString("PENY").toDouble()
                val rateList= arrayListOf<Double>(shilRate,dolrRate,quidRate,penyRate)
                val adapter=RateAdapter(this,rateList)
                listView.adapter = adapter
                listView.onItemClickListener=AdapterView.OnItemClickListener{adapterView, view, position, id->
                    //TODO:CONVERT TO GOLD
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

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.fragment_frame_bank,fragment)
            addToBackStack(null)// press the Back on the phone and return to the main screen
            commit()
        }
    }


}
