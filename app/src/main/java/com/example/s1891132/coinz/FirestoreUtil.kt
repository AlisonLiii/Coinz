package com.example.s1891132.coinz

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.longToast

/*
 * Acknowledgment:Codes adapted from Firebase Firestore Chat App: User Profile (Ep 2) - Kotlin Android Tutorial
 * https://www.youtube.com/watch?v=eHA9jGT_87Q
 */


object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }//using by lazy means the value of firebaseFirestore get instance only upon first access
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().uid //identify each single user by uid allocated by firebase authentication
                ?: throw NullPointerException("UID is null")}")//If user not sign in, it's gonna be NULL

    //Initialise the user profile in Firestore when he/she first sign in;
    // if he/she has signed in before on another device, this method would not call
    fun initCurrentUserIfFirstTime(name:String,camp:Int, onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {//Create user profile if the user's profile doesn't exist before
                val newUser = CoinzUser(name, FirebaseAuth.getInstance().currentUser!!.uid , FirebaseAuth.getInstance().currentUser!!.email!!, "", camp,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0)//TODO:maybe a null-safety problem here
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            } else
                onComplete()
        }
    }

    //TODO:update account balance
    fun updateCurrentUserProfile(name:String="",id:String="",email:String="",bio:String="",camp:Int=2)
    {
        val userFiledMap= mutableMapOf<String,Any>()
        if(name.isNotBlank()) userFiledMap["name"]=name
        if(bio.isNotBlank()) userFiledMap["bio"]=bio
        currentUserDocRef.update(userFiledMap)
    }

    fun updateWalletBalance(peny:Double=0.0,dolr:Double=0.0,shil:Double=0.0,quid:Double=0.0,operation:Int)
    {
        currentUserDocRef.get().addOnSuccessListener { document ->
            if(document!=null)
            {
                val originPeny=document["walletPeny"] as Double
                val originDolr=document["walletDolr"] as Double
                val originShil=document["walletShil"] as Double
                val originQuid=document["walletQuid"] as Double
                Log.i("wallet test",originPeny.toString())
                val walletFileMap= mutableMapOf<String,Any>()
                if(operation==1)//plus
                {
                    if(peny!=0.0) walletFileMap["walletPeny"]= originPeny+peny
                    if(dolr!=0.0) walletFileMap["walletDolr"]=originDolr+dolr
                    if(shil!=0.0) walletFileMap["walletShil"]=originShil+shil
                    if(quid!=0.0) walletFileMap["walletQuid"]=originQuid+quid
                    currentUserDocRef.update(walletFileMap)
                }
                else if(operation==-1)//minus
                {
                    if(peny!=0.0) walletFileMap["walletPeny"]= originPeny-peny
                    if(dolr!=0.0) walletFileMap["walletDolr"]=originDolr-dolr
                    if(shil!=0.0) walletFileMap["walletShil"]=originShil-shil
                    if(quid!=0.0) walletFileMap["walletQuid"]=originQuid-quid
                    currentUserDocRef.update(walletFileMap)
                }
                else
                    Log.d("updateWallet","No such operations")
            }
            else{
                Log.d("updateWallet","No such document")
            }
        }
    }




    fun getCurrentUser(onComplete: (CoinzUser) ->Unit){
        currentUserDocRef.get()
                .addOnSuccessListener {
                    onComplete(it.toObject(CoinzUser::class.java)!!)//TODO:NO !! HERE IN THE VIDEO
                }
    }
}