package com.example.s1891132.coinz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import org.jetbrains.anko.longToast


class LogInActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    // id should be returned to Mainactivity, perhaps constructing a user class.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        mAuth= FirebaseAuth.getInstance()
        //there's a bug here, the edit text is blank?

       /* if(username_text.text.isBlank()||password_text.text.isBlank()){
            signin.isClickable=false
        }*/



        signin.setOnClickListener{
            val email=username_text.text.toString()
            val password=password_text.text.toString()

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful()) {
                    var intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("id", mAuth.currentUser?.email)//id or email address
                    startActivity(intent)//you cannot go back to mainActivity.....because you still need to login again.
                } else {
                    longToast("Invalid username or password, please try again")
                }
            })
        }//here you should test what if invalid password?
       register.setOnClickListener {
            startActivity(Intent(this@LogInActivity,RegisterActivity::class.java))
        }

    }


}
