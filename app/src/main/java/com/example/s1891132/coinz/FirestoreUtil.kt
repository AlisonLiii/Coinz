package com.example.s1891132.coinz

import android.content.Context
import android.util.Log
import android.view.View
import com.example.s1891132.coinz.dataClassAndItem.Coin
import com.example.s1891132.coinz.dataClassAndItem.CoinzUser
import com.example.s1891132.coinz.dataClassAndItem.PersonItem
import com.example.s1891132.coinz.message.ChatChannel
import com.example.s1891132.coinz.message.TextMessage
import com.example.s1891132.coinz.message.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item
import org.jetbrains.anko.design.snackbar

/*
 * Acknowledgment:Codes adapted from Firebase Firestore Chat App: User Profile (Ep 2) - Kotlin Android Tutorial
 * https://www.youtube.com/watch?v=eHA9jGT_87Q
 */



object FirestoreUtil {
    //using by lazy means the value of firebaseFirestore get instance only upon first access
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    //identify each single user by uid allocated by firebase authentication
    val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().uid
                ?: throw NullPointerException("UID is null")}")//If user doesn't sign in, it's gonna be null

    //Collection reference of the coins list that contains coinz user himself collect
    val coinSelfCollectListRef: CollectionReference
        get()= currentUserDocRef.collection("coinListForTheDay")

    //Collection reference of the coins list that contains coinz user receive from others
    val coinFromOthersRef: CollectionReference
        get()= currentUserDocRef.collection("coinFromOthers")

    //Collection reference of the marker information list that contains coinz user himself has collected
    val markerRef: CollectionReference
        get()= currentUserDocRef.collection("markerForTheDay")


    val aiGoldRef:DocumentReference
        get() =  firestoreInstance.collection("ForRanking").document("AI")
    val humanGoldRef:DocumentReference
        get() =  firestoreInstance.collection("ForRanking").document("Human")
    val individualGoldRef:DocumentReference
        get() =  firestoreInstance.collection("ForRanking").document("IndividualGold")


    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")


    /****
     * User Profile Pattern
     */

    //Initialise the user profile in Firestore when he/she first sign in;
    // if he/she has signed in before on another device, this method would not call
    fun initCurrentUserIfFirstTime(name:String,camp:Double, onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {//Create user profile if the user's profile doesn't exist before
                val newUser = CoinzUser(name, FirebaseAuth.getInstance().currentUser!!.uid, FirebaseAuth.getInstance().currentUser!!.email!!, "", camp, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, currentDate(), 0.0)//TODO:maybe a null-safety problem here
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            } else
                onComplete()
        }
    }

    fun updateCurrentUserProfile(name:String="",bio:String="")
    {
        val userFiledMap= mutableMapOf<String,Any>()
        if(name.isNotBlank()) userFiledMap["name"]=name
        if(bio.isNotBlank()) userFiledMap["bio"]=bio
        currentUserDocRef.update(userFiledMap)
    }


    /***
     * Bank Account Pattern
     */


    //record the number of self-collected coinz user has banked into his account of the day
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


    fun convertToGold(view: View?,type:String,rate:Double){
        currentUserDocRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var moneyToConvert=0.0
                if(type.equals("SHIL",true))
                    moneyToConvert=document["accountShil"] as Double
                else if (type.equals("PENY",true))
                    moneyToConvert = document["accountPeny"] as Double
                else if (type.equals("DOLR",true))
                    moneyToConvert = document["accountDolr"] as Double
                else
                    moneyToConvert = document["accountQuid"] as Double

                if(moneyToConvert<=0.0)
                    view?.snackbar("You don't have coinz of that type to convert in your account!")
                else{
                    val originGold=document["accountGold"] as Double

                    val accountGoldMap = mutableMapOf<String, Any>()
                    val value=moneyToConvert*rate+originGold
                    accountGoldMap["accountGold"]=value
                    currentUserDocRef.update(accountGoldMap)

                    //After converting, zero out the type of currency in the account
                    if(type.equals("SHIL",true))
                        updateAccountBalance(0.0,0.0,moneyToConvert,0.0,-1)
                    else if (type.equals("PENY",true))
                        updateAccountBalance(moneyToConvert,0.0,0.0,0.0,-1)
                    else if (type.equals("DOLR",true))
                        updateAccountBalance(0.0,moneyToConvert,0.0,0.0,-1)
                    else
                        updateAccountBalance(0.0,0.0,0.0,moneyToConvert,-1)

                    view?.snackbar("Successfully convert!")

                    /*individualGoldRef.get().addOnSuccessListener { document->
                        if(document!=null)
                        {
                            val originIndiGold=document["accountGold"] as Double
                            if(originIndiGold<value)
                            {
                                val indiMap= mutableMapOf<String, Any>()
                                indiMap["accountGold"]=value
                                individualGoldRef.update(indiMap)
                            }
                        }
                    }*/
                }

                //TODO:INIT in the app to make it double
                /*if(document["camp"]==0.0)
                {
                    aiGoldRef.get().addOnSuccessListener {document->
                        if(document!=null)
                        {
                            val originAiGold=document["accountGold"] as Double
                            val aiMap= mutableMapOf<String, Any>()
                            aiMap["accountGold"]=originAiGold+moneyToConvert*rate
                            aiGoldRef.update(aiMap)
                        }
                    }
                }
                else{
                    humanGoldRef.get().addOnSuccessListener {document->
                        if(document!=null)
                        {
                            val originHumanGold=document["accountGold"] as Double
                            val humanMap= mutableMapOf<String, Any>()
                            humanMap["accountGold"]=originHumanGold+moneyToConvert*rate
                            humanGoldRef.update(humanMap)
                        }
                    }

                }*/

            }
        }
    }


    /***
     * List operation Pattern
     */

    //delete coins in lists (collections in Firestore)
    fun deleteCoinInList(listWithCoins:CollectionReference,id: String){
        listWithCoins.document(id).delete()
    }

    //add coins in lists(collections in Firestore
    fun addCoinInList(collectionReference: CollectionReference,coin: Coin)
    {
        collectionReference.document(coin.id).get().addOnSuccessListener { documentSnapshot ->
            if(!documentSnapshot.exists()) {
                collectionReference.document(coin.id).set(coin)
            }
        }
    }


    /**
     * Share coins to friends Pattern
     */
    fun shareCoinz(view: View, otherID:String, coin: Coin){

        val otherUserDocRef= firestoreInstance.collection("users").document(otherID)
        //The collection reference of coins from others
        val otherUserCollRef= otherUserDocRef.collection("coinFromOthers")

        //add coins in user's friend's list
        otherUserCollRef.document(coin.id).collection("users").document(otherID)
                .get().addOnSuccessListener { documentSnapshot ->
            if(!documentSnapshot.exists()) {
                otherUserCollRef.document(coin.id).set(coin)
            }
            else {
                //user cannot own the same coin from more than one friend
                //But he can receive the coin from B after he bank the coin with the same id from A into his account
                view.snackbar("Sent fail. Your friend already has this coin from other friends! Tell him to bank that coin into account first, and you can resend the coin")
            }
        }
        //also, user can only share coins he collect himself to his friends.

        //delete coin in user's list
        deleteCoinInList(FirestoreUtil.coinSelfCollectListRef,coin.id)

        //update the wallet balance of user's and his friend's
        if(coin.type.equals("PENY",true))
        {
            updateWalletBalance(currentUserDocRef,coin.value,0.0,0.0,0.0,-1)
            updateWalletBalance(otherUserDocRef,coin.value,0.0,0.0,0.0,1)
        }
        else if(coin.type.equals("DOLR",true))
        {
            updateWalletBalance(currentUserDocRef,0.0,coin.value,0.0,0.0,-1)
            updateWalletBalance(otherUserDocRef,0.0,coin.value,0.0,0.0,1)
        }
        else if(coin.type.equals("SHIL",true))
        {
            updateWalletBalance(currentUserDocRef,0.0,0.0,coin.value,0.0,-1)
            updateWalletBalance(otherUserDocRef,0.0,0.0,coin.value,0.0,1)
        }
        else
        {
            updateWalletBalance(currentUserDocRef,0.0,0.0,0.0,coin.value,-1)
            updateWalletBalance(otherUserDocRef,0.0,0.0,0.0,coin.value,1)
        }
    }

    /**
     * record walking distance in Firestore
     */

    fun updateWalkingDistance(walkingDistance:Double){
        val userFileMap= mutableMapOf<String,Any>()
        userFileMap["walkingDistance"]=walkingDistance
        currentUserDocRef.update(userFileMap)
    }

    /***
     * local wallet pattern
     */

    fun updateWalletBalance(documentReference: DocumentReference,peny:Double=0.0,dolr:Double=0.0,shil:Double=0.0,quid:Double=0.0,operation:Int)
    {
        documentReference.get().addOnSuccessListener { document ->
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
                    documentReference.update(walletFileMap)
                }
                else if(operation==-1)//minus
                {
                    if(peny!=0.0) walletFileMap["walletPeny"]= originPeny-peny
                    if(dolr!=0.0) walletFileMap["walletDolr"]=originDolr-dolr
                    if(shil!=0.0) walletFileMap["walletShil"]=originShil-shil
                    if(quid!=0.0) walletFileMap["walletQuid"]=originQuid-quid
                    documentReference.update(walletFileMap)
                }
                else if(operation==0)//zero out
                {
                    walletFileMap["walletPeny"]=0.0
                    walletFileMap["walletDolr"]=0.0
                    walletFileMap["walletShil"]=0.0
                    walletFileMap["walletQuid"]=0.0
                    documentReference.update(walletFileMap)
                }
                else
                    Log.d("updateWallet","No such operations")
            }
            else{
                Log.d("updateWallet","No such document")
            }
        }
    }


    /***
     * If a new day comes, the coins in user's wallet, user's walking distance,
     * the coins will all expire
     */
    fun newDayUpdateOrNot()
    {
        //TODO:NOTICE BOARD UPDATE
        getCurrentUser { CoinzUser ->
            if(CoinzUser.date!= currentDate())
            {
                //zero out user's wallet
                updateWalletBalance(currentUserDocRef,0.0,0.0,0.0,0.0,0)

                //delete all the coinz
                coinSelfCollectListRef.get().addOnSuccessListener{documents->
                    for(document in documents){
                       coinSelfCollectListRef.document(document.id).delete()
                    }
                }
                coinFromOthersRef.get().addOnSuccessListener{documents->
                    for(document in documents){
                        coinFromOthersRef.document(document.id).delete()
                    }
                }
                markerRef.get().addOnSuccessListener{documents->
                    for(document in documents){
                        markerRef.document(document.id).delete()
                    }
                }

                //update the information of the new day
                val userFileMap= mutableMapOf<String,Any>()
                userFileMap["date"]= currentDate()
                userFileMap["bankNum"]= 0.0
                userFileMap["walkingDistance"]=0.0
                currentUserDocRef.update(userFileMap)
            }
        }
    }


    /****
     * get current user
     */
    fun getCurrentUser(onComplete: (CoinzUser) ->Unit){
        currentUserDocRef.get()
                .addOnSuccessListener {
                    onComplete(it.toObject(CoinzUser::class.java)!!)
                }
    }

    /**
     * To get all the registered users from Firestore
     * */
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
                            items.add(PersonItem(it.toObject(CoinzUser::class.java)!!, context))
                    }
                    onListen(items)
                }
    }

    fun removeListener(registration: ListenerRegistration)=registration.remove()

    /**
     *Chatting pattern
     ***/

    fun getOrCreateChatChannel(otherUserId: String,
                               onComplete: (channelId: String) -> Unit) {
        //get a existing channel
        currentUserDocRef.collection("engagedChatChannels")
                .document(otherUserId).get().addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(it["channelId"] as String)
                        return@addOnSuccessListener
                    }

                    //init a new channel
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