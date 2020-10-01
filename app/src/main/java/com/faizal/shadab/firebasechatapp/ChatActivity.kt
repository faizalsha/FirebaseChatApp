package com.faizal.shadab.firebasechatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.faizal.shadab.firebasechatapp.firebaseUtil.FireStoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item
import org.jetbrains.anko.toast


class ChatActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstant.USER_NAME)

        val otherUserId = intent.getStringExtra(AppConstant.USER_ID)
        FireStoreUtil.getOrCreateChatChannel(otherUserId){channelId ->
            messagesListenerRegistration =
                FireStoreUtil.addChatMessagesListener(channelId, this, this::onMessagesChanged)
        }
    }
    private fun onMessagesChanged(messages: List<Item>){
        toast("onMessagesChanges")
    }
}