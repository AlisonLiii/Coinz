package com.example.s1891132.coinz

import android.content.Context
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.Toast
import com.example.s1891132.coinz.message.ChatChannel
import com.example.s1891132.coinz.message.TextMessage
import com.example.s1891132.coinz.message.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item
import org.jetbrains.anko.longToast

/*
 * Acknowledgment:Codes adapted from Firebase Firestore Chat App: User Profile (Ep 2) - Kotlin Android Tutorial
 * https://www.youtube.com/watch?v=eHA9jGT_87Q
 */


object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }//using by lazy means the value of firebaseFirestore get instance only upon first access
    val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().uid //identify each single user by uid allocated by firebase authentication
                ?: throw NullPointerException("UID is null")}")//If user not sign in, it's gonna be NULL
   val coinListRef: CollectionReference
        get()= currentUserDocRef.collection("coinListForTheDay")

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")
    //Initialise the user profile in Firestore when he/she first sign in;
    // if he/she has signed in before on another device, this method would not call
    fun initCurrentUserIfFirstTime(name:String,camp:Int, onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {//Create user profile if the user's profile doesn't exist before
                val newUser = CoinzUser(name, FirebaseAuth.getInstance().currentUser!!.uid , FirebaseAuth.getInstance().currentUser!!.email!!, "", camp,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0, currentDate())//TODO:maybe a null-safety problem here
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            } else
                onComplete()
        }
    }//TODO:ADD IN MAINACTIVITY?

    fun updateBankNumToday(){
        currentUserDocRef.get().addOnSuccessListener {document->
            if(document!=null)
            {
                val originBankNum=document["bankNum"] as Double
                val bankNumMap= mutableMapOf<String,Any>()
                bankNumMap["bankNum"]=originBankNum+1.0
                currentUserDocRef.update(bankNumMap)
            }
        }
    }

    fun deleteCoinInList(listWithCoins:CollectionReference,id: String){
        listWithCoins.document(id).delete()

    }

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
                else if(operation==0)//zero out
                {
                    walletFileMap["walletPeny"]=0.0
                    walletFileMap["walletDolr"]=0.0
                    walletFileMap["walletShil"]=0.0
                    walletFileMap["walletQuid"]=0.0
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

    fun updateAccountBalance(peny:Double=0.0,dolr:Double=0.0,shil:Double=0.0,quid:Double=0.0,operation:Int)
    {
        currentUserDocRef.get().addOnSuccessListener { document ->
            if(document!=null)
            {
                val originPeny=document["accountPeny"] as Double
                val originDolr=document["accountDolr"] as Double
                val originShil=document["accountShil"] as Double
                val originQuid=document["accountQuid"] as Double
                val accountFileMap= mutableMapOf<String,Any>()
                if(operation==1)//plus
                {
                    if(peny!=0.0) accountFileMap["accountPeny"]= originPeny+peny
                    if(dolr!=0.0) accountFileMap["accountDolr"]=originDolr+dolr
                    if(shil!=0.0) accountFileMap["accountShil"]=originShil+shil
                    if(quid!=0.0) accountFileMap["accountQuid"]=originQuid+quid
                    currentUserDocRef.update(accountFileMap)
                }
                else if(operation==-1)//minus
                {
                    if(peny!=0.0) accountFileMap["accountPeny"]= originPeny-peny
                    if(dolr!=0.0) accountFileMap["accountDolr"]=originDolr-dolr
                    if(shil!=0.0) accountFileMap["accountShil"]=originShil-shil
                    if(quid!=0.0) accountFileMap["accountQuid"]=originQuid-quid
                    currentUserDocRef.update(accountFileMap)
                }
                else if(operation==0)//zero out
                {
                    accountFileMap["accountPeny"]=0.0
                    accountFileMap["accountDolr"]=0.0
                    accountFileMap["accountShil"]=0.0
                    accountFileMap["accountQuid"]=0.0
                    currentUserDocRef.update(accountFileMap)
                }
                else
                    Log.d("updateAccount","No such operations")
            }
            else{
                Log.d("updateAccount","No such document")
            }
        }
    }

    fun addCoinInList(coin:Coin)
    {
        coinListRef.document(coin.id).get().addOnSuccessListener { documentSnapshot ->
            if(!documentSnapshot.exists()) {
                coinListRef.document(coin.id).set(coin)
            }
            else {
                //TODO:cannot collect the coin
            }
        }
    }


    fun newDayUpdateOrNot()
    {
        getCurrentUser { CoinzUser ->
            if(CoinzUser.date!= currentDate())
            {
                updateWalletBalance(0.0,0.0,0.0,0.0,0)
                coinListRef.get().addOnSuccessListener{documents->
                    for(document in documents){
                        coinListRef.document(document.id).delete()
                    }
                }
            }
        }
    }




    fun getCurrentUser(onComplete: (CoinzUser) ->Unit){
        currentUserDocRef.get()
                .addOnSuccessListener {
                    onComplete(it.toObject(CoinzUser::class.java)!!)//TODO:NO "!!" HERE IN THE VIDEO
                }
    }

    fun addUsersListener(context: Context,onListen:(List<Item>)->Unit):ListenerRegistration{
        return firestoreInstance.collection("users")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException!=null)
                    {
                        Log.e("Firestore","Users listener error.",firebaseFirestoreException)
                        return@addSnapshotListener
                    }
                    val items= mutableListOf<Item>()
                    querySnapshot!!.documents.forEach {
                        if(it.id!=FirebaseAuth.getInstance().currentUser?.uid)
                            items.add(PersonItem(it.toObject(CoinzUser::class.java)!!,context))
                    }
                    onListen(items)

                }
    }

    fun removeListener(registration: ListenerRegistration)=registration.remove()

    fun getOrCreateChatChannel(otherUserId: String,
                               onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection("engagedChatChannels")
                .document(otherUserId).get().addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(it["channelId"] as String)
                        return@addOnSuccessListener
                    }

                    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                    val newChannel = chatChannelsCollectionRef.document()
                    newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                    currentUserDocRef
                            .collection("engagedChatChannels")
                            .document(otherUserId)
                            .set(mapOf("channelId" to newChannel.id))

                    firestoreInstance.collection("users").document(otherUserId)
                            .collection("engagedChatChannels")
                            .document(currentUserId)
                            .set(mapOf("channelId" to newChannel.id))

                    onComplete(newChannel.id)
                }
    }

    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
                .orderBy("time")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()
                    querySnapshot!!.documents.forEach {
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                        return@forEach
                    }
                    onListen(items)
                }
    }

    fun sendMessage(message: TextMessage,channelId: String){
        chatChannelsCollectionRef.document(channelId).collection("messages").add(message)
    }

}