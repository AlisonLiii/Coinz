package com.example.s1891132.coinz

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.s1891132.coinz.message.AppConstants
import com.example.s1891132.coinz.message.TextMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.design.snackbar
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        contentView?.snackbar("You can share your coins to your friends by clicking the coinz button at the corner!")

        //get other user's id from intent
        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)

        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            //update the message dynamically
            messagesListenerRegistration=FirestoreUtil.addChatMessagesListener(channelId,this,this::updateRecyclerView)

            //click on the icon to send message
            imageView_send.setOnClickListener {
                val messageToSend=TextMessage(editText_message.text.toString(), Calendar.getInstance().time,FirebaseAuth.getInstance().currentUser!!.uid)
                //clear the message after clicking "sent"
                editText_message.setText("")
                FirestoreUtil.sendMessage(messageToSend,channelId)
            }

            //click on the icon to share coinz to your friend
            fab_send_coin.setOnClickListener {
                val intent= Intent(this,ShareCoinzActivity::class.java)
                intent.putExtra("RecipentID",otherUserId)
                startActivity(intent)//go to share coinz activity
            }
        }

    }

    private fun updateRecyclerView(messages:List<Item>) {
        //init message item
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        //update message item
        fun updateItems() = messagesSection.update(messages)

        if(shouldInitRecyclerView)
            init()
        else
            updateItems()

        //scroll down the the latest message
        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter.itemCount-1)
    }
}

