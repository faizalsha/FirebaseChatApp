package com.faizal.shadab.firebasechatapp.ui.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.faizal.shadab.firebasechatapp.AppConstant
import com.faizal.shadab.firebasechatapp.ChatActivity
import com.faizal.shadab.firebasechatapp.R
import com.faizal.shadab.firebasechatapp.firebaseUtil.FireStoreUtil
import com.faizal.shadab.firebasechatapp.recyclerview.PersonItem
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.support.v4.startActivity

class PeopleFragment : Fragment() {

    private lateinit var peopleViewModel: PeopleViewModel

    private lateinit var userListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        peopleViewModel =
            ViewModelProviders.of(this).get(PeopleViewModel::class.java)
        peopleViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        userListenerRegistration = FireStoreUtil.addUsersListener(requireContext(), this::updateRecyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FireStoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {

        fun init() {
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClickListener)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

    }

    private val onItemClickListener = OnItemClickListener{item, view ->
        if(item is PersonItem){
            startActivity<ChatActivity>(
                AppConstant.USER_NAME to item.person.name,
                AppConstant.USER_ID to item.userId
            )
        }
    }
}
