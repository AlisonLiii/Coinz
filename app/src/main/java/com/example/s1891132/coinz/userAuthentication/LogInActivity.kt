package com.example.s1891132.coinz.userAuthentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.s1891132.coinz.MainActivity
import com.example.s1891132.coinz.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.design.longSnackbar


class LogInActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        //sign in
        mAuth= FirebaseAuth.getInstance()
        signin.setOnClickListener{
            val email=username_text.text.toString()
            val password=password_text.text.toString()
            if(email.isEmpty()||password.isEmpty()) {
                contentView?.longSnackbar("Please enter email address and password!")
                return@setOnClickListener
            }
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)//Successfully log in, go to main activity
                    startActivity(intent)
                } else {
                    contentView?.longSnackbar("Invalid username or password, please try again. Make sure you have registered before.")
                }
            }
        }

        //go to register
        register.setOnClickListener {
            startActivity(Intent(this@LogInActivity, RegisterActivity::class.java))//Go to register activity
        }

    }


}
