package com.example.s1891132.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.s1891132.coinz.userAuthentication.LogInActivity
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(FirebaseAuth.getInstance().currentUser==null)
            startActivity<LogInActivity>()
        else
            startActivity<MainActivity>()//you don't have to log in every time you open the app
        finish()
    }
}
