package com.example.s1891132.coinz.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.s1891132.coinz.FirestoreUtil
import com.example.s1891132.coinz.R
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import org.jetbrains.anko.design.snackbar


class MyAccountFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_my_account, container, false)
        view.apply {

            save_account_info.setOnClickListener{
                FirestoreUtil.updateCurrentUserProfile(edit_name.text.toString(), edit_bio.text.toString())
                view.snackbar("Changes saved!")//save the changes of modification on user profile
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        //show the user's profile information
        FirestoreUtil.getCurrentUser { CoinzUser ->
            if (this@MyAccountFragment.isVisible)
                edit_name.setText(CoinzUser.name)
            email_address_text.text = CoinzUser.email
            uid.text = CoinzUser.id
            edit_bio.setText(CoinzUser.bio)
            walking_distance_text.text = CoinzUser.walkingDistance.toString()
            if (CoinzUser.camp == 0.0)
                camp_text.text = ("AI")
            else
                camp_text.text = ("Human")
        }
    }
}
