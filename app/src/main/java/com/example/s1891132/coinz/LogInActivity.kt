package com.example.s1891132.coinz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {
    // id should be returned to Mainactivity, perhaps constructing a user class.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

       val email=username_text.text.toString()
        val password=password_text.text.toString()
       register.setOnClickListener {
            startActivity(Intent(this@LogInActivity,RegisterActivity::class.java))
        }
        //val a=username_text.text.toString()
        //edittext's name can use directly....without findview by id
    }
}
