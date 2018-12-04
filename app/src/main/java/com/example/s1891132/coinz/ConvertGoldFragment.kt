package com.example.s1891132.coinz


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_convert_gold.*
import org.json.JSONObject


class ConvertGoldFragment : Fragment() {

    private lateinit var settings: SharedPreferences
    private lateinit var coinInfo:String
    private var coinzFile="CoinzGeoInfoToday"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_convert_gold, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        val activity=activity as BankCoinzActivity
        settings=activity.getSharedPreferences(coinzFile, Context.MODE_PRIVATE)
        coinInfo=settings.getString(currentDate(),"")

        val jsonObject= JSONObject(coinInfo)
        val jsonRate=jsonObject.getJSONObject("rates")
        val shilRate=jsonRate.getString("SHIL")
        val dolrRate=jsonRate.getString("DOLR")
        val quidRate=jsonRate.getString("QUID")
        val penyRate=jsonRate.getString("PENY")
        shil.text=shilRate

    }


}
