package com.example.s1891132.coinz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LogInActivity : AppCompatActivity() {

    //ask for network permission here.........!!! PermissionManager
    private lateinit var signUpButton: Button
    private lateinit var registerButton: Button

    // id should be returned to Mainactivity, perhaps constructing a user class.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        //val a=username_text.text.toString()
        //edittext's name can use directly....without findview by id
    }
}
