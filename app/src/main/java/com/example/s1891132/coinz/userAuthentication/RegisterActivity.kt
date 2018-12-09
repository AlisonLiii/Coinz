package com.example.s1891132.coinz.userAuthentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.example.s1891132.coinz.FirestoreUtil
import com.example.s1891132.coinz.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var dialog:android.app.AlertDialog
    private lateinit var mAuth:FirebaseAuth
    private  var camp:Double=2.0//default value, means nothing. 0.0 means AI, 1.0 means human

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth= FirebaseAuth.getInstance()

        //choose camp

        //get the description of the game in alert dialog
       dialog=alert(getString(R.string.game_description)){
            title="About the Coinz game..."
            positiveButton("I choose to be AI"){camp=0.0 }
            negativeButton("I choose to be human"){camp=1.0 }
        }.show().apply {
            //to get two buttons look the same and do not focus on any of it. (The default setting is to focus on negative button)
            this.getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
                it.focusable = View.NOT_FOCUSABLE
            }
        }



        //go back to sign in activity
        back_to_signin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LogInActivity::class.java))
        }


        //submit register request
        registersubmit.setOnClickListener {
            val username=registerusername.text.toString()
            val email=registeremail.text.toString()
            val password=registerpassword.text.toString()

            if(username.isEmpty()||email.isEmpty()||password.isEmpty()) {
                toast("Please enter text in username/email/password")
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                if(task.isSuccessful)
                {
                    //Initialize user information in Firestore
                    FirestoreUtil.initCurrentUserIfFirstTime(username, camp) {
                        longToast("create account success.Go to log in now!")
                        startActivity(Intent(this@RegisterActivity, LogInActivity::class.java))//Go to log in activity
                    }

                }
                else{
                    contentView?.longSnackbar("Failed.The email address may have been registered or is not in valid format, or the password is too easy  ")
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
    }
}
