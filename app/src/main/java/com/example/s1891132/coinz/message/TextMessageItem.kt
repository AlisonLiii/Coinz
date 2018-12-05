package com.example.s1891132.coinz.message
import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.example.s1891132.coinz.R
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent
import java.text.SimpleDateFormat

//This class is based on the tutorial below.
//https://www.youtube.com/watch?v=ybS6epU1NGQ&list=PLB6lc7nQ1n4h5tzT3tu_YSy9VNrVUR_4W&index=5

class TextMessageItem(val message: TextMessage,
                      val context: Context)
    : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_message_text.text = message.text
        setTimeText(viewHolder)
        setMessageRootGravity(viewHolder)
    }

    private fun setTimeText(viewHolder: ViewHolder){
        val dateFormat=SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT,SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text=dateFormat.format(message.time)//get current time for the message
    }

    private fun setMessageRootGravity(viewHolder: ViewHolder) {
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid)//I send the message
        {
            viewHolder.message_root.apply {
                //adjust the frame layout: my message is shown on the right of the screen and in white background
                backgroundResource = R.drawable.rect_round_white
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END)
                this.layoutParams = lParams
            }
        } else {
            viewHolder.message_root.apply {
                //adjust the frame layout: the message I receive is shown on the left of the screen and in orange background
                backgroundResource = R.drawable.rect_round_primary_color
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                this.layoutParams = lParams
            }
        }
    }

    override fun getLayout() = R.layout.item_text_message

}