package com.example.s1891132.coinz.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.s1891132.coinz.ChatActivity
import com.example.s1891132.coinz.FirestoreUtil
import com.example.s1891132.coinz.R
import com.example.s1891132.coinz.ShareCoinzActivity
import com.example.s1891132.coinz.dataClassAndItem.PersonItem
import com.example.s1891132.coinz.message.AppConstants
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.startActivity


class PeopleFragment : Fragment() {

    //This class is adapted from the tutorial below
    //https://www.youtube.com/watch?v=a9I7Ppzh1_Y&index=3&list=PLB6lc7nQ1n4h5tzT3tu_YSy9VNrVUR_4W

    private lateinit var userListenerRegistration:ListenerRegistration //for getting a list of all the users
    private lateinit var peopleSection: Section

    private var shouldInitRecyclerView = true


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view=inflater.inflate(R.layout.fragment_people, container, false)
        val searchButton=view.findViewById<ImageButton>(R.id.imageView_email_search)
        val searchText=view.findViewById<TextInputEditText>(R.id.editText_email_search)
        //listen to all the registered users
        userListenerRegistration = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecyclerView)
        view.apply {

            //search for a user with particular email address
            searchButton.setOnClickListener {
                if (searchText.text.isNullOrBlank()) {
                    view.snackbar("Enter email please")
                    return@setOnClickListener
                }
                FirestoreUtil.firestoreInstance.collection("users").whereEqualTo("email", searchText.text.toString())
                        .get().addOnSuccessListener {
                            if(it.isEmpty)
                                view.snackbar("No such user.")
                            else {
                                it.forEach {
                                    val id = it["id"] as String
                                    FirestoreUtil.getCurrentUser { CoinzUser ->
                                        if (CoinzUser.id != id)//the target user is not the current user itself
                                        {
                                            val intent = Intent(context, ShareCoinzActivity::class.java)//go to ShareCoinzActivity to share coinz
                                            intent.putExtra("RecipentID", id)
                                            context?.startActivity(intent)
                                        } else view.snackbar("you cannot search yourself!")
                                    }
                                }
                            }
                        }
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true//reinitialize the view after it has been destroyed
    }

    private fun updateRecyclerView(items: List<Item>) {
        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {//list all the people
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() =peopleSection.update(items)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()
        }


    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is PersonItem) {//go to ChatActivity after clicking on a person item
            startActivity<ChatActivity>(
                    AppConstants.USER_NAME to item.person.name,
                    AppConstants.USER_ID to item.person.id
            )
        }
    }

}
