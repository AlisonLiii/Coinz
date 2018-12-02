package com.example.s1891132.coinz

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
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
import java.util.ArrayList

class BankCoinzActivity : AppCompatActivity() {

    private lateinit var listView: ListView


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.put_into_account -> {

                listView.visibility=View.VISIBLE
                var coinSelfCollectList:ArrayList<Coin> = ArrayList()
                Log.i("testcoin","testt")
                FirestoreUtil.coinListRef.get()
                        .addOnSuccessListener {result->
                            for (document in result)
                            {
                                val coin=document.toObject(Coin::class.java)
                                coinSelfCollectList.add(coin)
                               // Log.i("testcoin",coin.id)
                            }
                            /*val listItems = arrayOfNulls<String>(coinSelfCollectList.size)
                            for (i in 0 until coinSelfCollectList.size) {
                                listItems[i] = coinSelfCollectList[i].type+"   value="+coinSelfCollectList[i].value.toString()
                            }
                            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,listItems)*/
                            val adapter=CoinAdapter(this,coinSelfCollectList)
                            listView.adapter = adapter

                            listView.onItemClickListener = AdapterView.OnItemClickListener{adapterView, view, position, id->
                                //TODO:bankNUm+1 on user
                                //TODO:DELETE THE COIN IN THE ARRAY
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
//TODO:more than 25 cannot collect
                                adapter.remove(position)
                            }
                        }



                //message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {//TODO:BANK COINZ that friend share
                //message.setText(R.string.title_dashboard)
                listView.visibility=View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {//TODO: CONVERT coinz into gold
                //message.setText(R.string.title_notifications)
                listView.visibility=View.INVISIBLE

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


}
