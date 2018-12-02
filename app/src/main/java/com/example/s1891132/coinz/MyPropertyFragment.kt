package com.example.s1891132.coinz


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_my_property.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.longToast


class MyPropertyFragment : Fragment() {//TODO:CLASH WIT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_property, container, false)
        val coin_button=view.findViewById<Button>(R.id.put_coin_in_bank) as Button
        coin_button.setOnClickListener {
            val intent=Intent(context,BankCoinzActivity::class.java)
            context?.startActivity(intent)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { CoinzUser->
            if(this@MyPropertyFragment.isVisible)
            {
                frag_account_dolr.setText("DOLR:"+CoinzUser.accountDolr.toString())
                frag_account_gold.setText("GOLR:"+CoinzUser.accountGold.toString())
                frag_account_peny.setText("PENY:"+CoinzUser.accountPeny.toString())
                frag_account_quid.setText("QUID:"+CoinzUser.accountQuid.toString())
                frag_account_shil.setText("SHIL:"+CoinzUser.accountShil.toString())
                frag_wallet_dolr.setText("DOLR:"+CoinzUser.walletDolr.toString())
                frag_wallet_peny.setText("PENY:"+CoinzUser.walletPeny.toString())
                frag_wallet_shil.setText("SHIL:"+CoinzUser.walletShil.toString())
                frag_wallet_quid.setText("QUID:"+CoinzUser.walletQuid.toString())//todo:Maybe problem in the future with subcollections
            }

        }
    }
}

