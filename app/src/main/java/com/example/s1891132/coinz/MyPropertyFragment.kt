package com.example.s1891132.coinz


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_property.*


class MyPropertyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_property, container, false)
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

