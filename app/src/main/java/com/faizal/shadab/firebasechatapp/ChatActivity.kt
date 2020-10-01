package com.faizal.shadab.firebasechatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.faizal.shadab.firebasechatapp.firebaseUtil.FireStoreUtil
import com.faizal.shadab.firebasechatapp.model.MessageType
import com.faizal.shadab.firebasechatapp.model.TextMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.toast
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messageSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstant.USER_NAME)

        val otherUserId = intent.getStringExtra(AppConstant.USER_ID)
        FireStoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            messagesListenerRegistration =
                FireStoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            imageView_send.setOnClickListener {
                val messageToSend =
                    TextMessage(
                        Calendar.getInstance().time,
                        MessageType.TEXT,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        editText_message.text.toString()
                    )
                editText_message.setText("")
                FireStoreUtil.sendMessage(messageToSend, channelId)
            }
        }
    }
    private fun updateRecyclerView(messages: List<Item>){
        fun init(){
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messageSection = Section(messages)
                    this.add(messageSection)
                }
            }
            shouldInitRecyclerView = false
        }
        fun updateItems() = messageSection.update(messages)

        if(shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount - 1)
    }
}