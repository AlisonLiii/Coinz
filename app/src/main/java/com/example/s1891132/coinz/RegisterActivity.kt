package com.example.s1891132.coinz

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.longToast

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth= FirebaseAuth.getInstance()
        //EDIT TEXT BLANK PROBLEM STILL EXISTS


        Log.i("test","here")
        registersubmit.setOnClickListener {
            val username=registerusername.text.toString()//how to add username?
            val email=registeremail.text.toString()
            val password=registerpassword.text.toString()
            Log.i("test",password)

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                Log.i("test","here2")
                if(task.isSuccessful)
                {
                    longToast("create account success.Check your mailbox to verify")
                    startActivity(Intent(this@RegisterActivity,LogInActivity::class.java))
                }
                else{
                    longToast("failed.Please retry.")
                }
            }
        }




    }
}
