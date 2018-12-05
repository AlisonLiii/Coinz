package com.example.s1891132.coinz


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_my_property.*



class MyPropertyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_my_property, container, false)
        val coinbutton=view.findViewById<Button>(R.id.put_coin_in_bank)
        coinbutton.setOnClickListener {
            val intent=Intent(context,BankCoinzActivity::class.java)//Go to BankCoinzActivity
            context?.startActivity(intent)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { CoinzUser->
            if(this@MyPropertyFragment.isVisible)
            {
                frag_account_dolr.text=CoinzUser.accountDolr.toString()
                frag_account_gold.text=CoinzUser.accountGold.toString()
                frag_account_peny.text=CoinzUser.accountPeny.toString()
                frag_account_quid.text=CoinzUser.accountQuid.toString()
                frag_account_shil.text=CoinzUser.accountShil.toString()
                frag_wallet_dolr.text=CoinzUser.walletDolr.toString()
                frag_wallet_peny.text=CoinzUser.walletPeny.toString()
                frag_wallet_shil.text=CoinzUser.walletShil.toString()
                frag_wallet_quid.text=CoinzUser.walletQuid.toString()
            }

        }
    }
}

