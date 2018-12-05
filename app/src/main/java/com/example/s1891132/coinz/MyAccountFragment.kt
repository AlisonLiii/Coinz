package com.example.s1891132.coinz


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import kotlinx.coroutines.experimental.selects.select
import org.jetbrains.anko.support.v4.longToast
import java.io.ByteArrayOutputStream

class MyAccountFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_my_account, container, false)
        view.apply {

            save_account_info.setOnClickListener{
                FirestoreUtil.updateCurrentUserProfile(edit_name.text.toString(),"","",
                                edit_bio.text.toString(),2)
                longToast("Changes saved")
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { CoinzUser->
            if(this@MyAccountFragment.isVisible)
                edit_name.setText(CoinzUser.name)
                email_address_text.setText(CoinzUser.email)
                uid.setText(CoinzUser.id)
                edit_bio.setText(CoinzUser.bio)
                walking_distance_text.setText(CoinzUser.walkingDistance.toString())
                if(CoinzUser.camp==0.0)
                    camp_text.setText("AI")
                else
                    camp_text.setText("Human")
        }
    }
}
