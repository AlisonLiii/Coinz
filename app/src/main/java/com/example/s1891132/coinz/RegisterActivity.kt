package com.example.s1891132.coinz

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth:FirebaseAuth
    private  var camp:Int=2
    private lateinit var username:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth= FirebaseAuth.getInstance()
        alert("You can choose between two camps: AI and human"){
            title="About the coinz game"
            positiveButton("I choose AI"){camp=0 }
            negativeButton("I choose human"){camp=1
            }
        }.show().apply {
            getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
                it.focusable = View.NOT_FOCUSABLE
            }
            getButton(AlertDialog.BUTTON_POSITIVE)?.let{
                it.focusable=View.NOT_FOCUSABLE
            }
        }
            /*alert("You can choose between two camps: AI and human", "About the Coinz game") {
                positiveButton("I choose AI",{ dialog ->dialog.dismiss() })
                negativeButton("I choose Human",{dialog->dialog.dismiss() })
            }.show().apply { getButton(AlertDialog.BUTTON_NEGATIVE)?.let{
                it.focusable= View.NOT_FOCUSABLE } }*/
        back_to_signin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity,LogInActivity::class.java))
        }
        registersubmit.setOnClickListener {
            username=registerusername.text.toString()
            val email=registeremail.text.toString()
            val password=registerpassword.text.toString()
            if(username.isEmpty()||email.isEmpty()||password.isEmpty()) {
                toast("Please enter text in username/email/password")
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                if(task.isSuccessful)
                {
                    FirestoreUtil.initCurrentUserIfFirstTime(username,camp){
                        longToast("create account success.Go to log in now!")
                        startActivity(Intent(this@RegisterActivity,LogInActivity::class.java))
                    }

                }
                else{
                    longToast("Failed.Please retry.The email address you enter may be registered before")
                }
            }
        }

    }
}
