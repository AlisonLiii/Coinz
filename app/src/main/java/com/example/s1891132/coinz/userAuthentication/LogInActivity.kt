package com.example.s1891132.coinz.userAuthentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.s1891132.coinz.MainActivity
import com.example.s1891132.coinz.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast


class LogInActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    // id should be returned to Mainactivity, perhaps constructing a user class.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        mAuth= FirebaseAuth.getInstance()
        signin.setOnClickListener{
            val email=username_text.text.toString()
            val password=password_text.text.toString()
            if(email.isEmpty()||password.isEmpty()) {
                toast("Please enter text in email/password")
                return@setOnClickListener
            }
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful()) {
                    var intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("id", mAuth.currentUser?.email)//id or email address
                    startActivity(intent)//you cannot go back to mainActivity.....because you still need to login again.
                } else {
                    longToast("Invalid username or password, please try again. Make sure you have registered before.")
                }
            })
        }
       register.setOnClickListener {
            startActivity(Intent(this@LogInActivity, RegisterActivity::class.java))
        }

    }


}
