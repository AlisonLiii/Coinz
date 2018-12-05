package com.example.s1891132.coinz


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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

    private lateinit var userListenerRegistration:ListenerRegistration //for getting full list of people
    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_people, container, false)
        val searchButton=view.findViewById<ImageButton>(R.id.imageView_email_search)
        val searchText=view.findViewById<TextInputEditText>(R.id.editText_email_search)
        userListenerRegistration = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecyclerView)
        view.apply {
            searchButton.setOnClickListener {
                if (searchText.text.isNullOrBlank()) {
                    snackbar(view,"Enter email please")
                    return@setOnClickListener
                }
                var id = "nouserwiththisemail"
                FirestoreUtil.firestoreInstance.collection("users").whereEqualTo("email", searchText.text.toString())
                        .get().addOnSuccessListener {
                            if(it.isEmpty)
                                snackbar(view,"No such user")
                            else {
                                it.forEach {
                                    id = it["id"] as String
                                    FirestoreUtil.getCurrentUser { CoinzUser->
                                        if(CoinzUser.id!=id)
                                        {
                                            val intent= Intent(context, ShareCoinzActivity::class.java)
                                            intent.putExtra("RecipentID",id)
                                            context?.startActivity(intent)
                                        }
                                        else snackbar(view,"you cannot search yourself")
                                    }
                                }
                            }
                        }
            }
        }
        return view
    }
    //TODO:too slow!

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true//reinitialize the view after it has been destroyed
    }

    private fun updateRecyclerView(items: List<Item>) {
        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
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


    private val onItemClick = OnItemClickListener { item, view ->
        if (item is PersonItem) {
            startActivity<ChatActivity>(
                    AppConstants.USER_NAME to item.person.name,
                    AppConstants.USER_ID to item.person.id
            )
        }
    }

}
