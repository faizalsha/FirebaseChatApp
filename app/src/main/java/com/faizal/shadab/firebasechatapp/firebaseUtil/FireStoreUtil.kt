package com.faizal.shadab.firebasechatapp.firebaseUtil

import android.content.Context
import android.util.Log
import com.faizal.shadab.firebasechatapp.AppConstant
import com.faizal.shadab.firebasechatapp.model.ChatChannel
import com.faizal.shadab.firebasechatapp.model.MessageType
import com.faizal.shadab.firebasechatapp.model.TextMessage
import com.faizal.shadab.firebasechatapp.model.User
import com.faizal.shadab.firebasechatapp.recyclerview.PersonItem
import com.faizal.shadab.firebasechatapp.recyclerview.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.kotlinandroidextensions.Item
import org.jetbrains.anko.appcompat.v7.Appcompat
import java.lang.NullPointerException

object FireStoreUtil {
    private val fireStoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = fireStoreInstance.document("${AppConstant.USERS}/${FirebaseAuth.getInstance().uid
            ?: throw NullPointerException("UID is null")}")

    private val chatChannelCollectionRef = fireStoreInstance.collection(AppConstant.chatChannels)

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit){
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if(!documentSnapshot.exists()){
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: ""
                                , "", null)
                println("debgug: i am here creating new user")
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    println("debgu: i am here creating new user")
                    onComplete()
                }
            }
            else{
                Log.d("else","see else part")
                onComplete()
            }

        }
    }

    fun updateCurrentUser(name: String = "", status: String = "", profilePicturePath: String? = null){
        val userFieldMap = mutableMapOf<String, Any>()
        if(name.isNotBlank()) userFieldMap[AppConstant.name] = name
        if(status.isNotBlank()) userFieldMap[AppConstant.status] = status
        if(profilePicturePath != null)
            userFieldMap[AppConstant.profilePicturePath] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User?) -> Unit){
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java))
            }
    }

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return fireStoreInstance.collection(AppConstant.USERS)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
                }
                onListen(items)
            }
    }
    //todo : check whether this acutally remove the listener as the listener pass by copy
    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannel(otherUserId: String,
                               onComplete: (channelId: String) -> Unit){
        currentUserDocRef.collection(AppConstant.engagedChannel)
            .document(otherUserId).get().addOnSuccessListener {
                if(it.exists()){
                    onComplete(it[AppConstant.channelId] as String)
                    return@addOnSuccessListener
                }
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                val newChannel = chatChannelCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDocRef.collection(AppConstant.engagedChannel)
                    .document(otherUserId)
                    .set(mapOf(AppConstant.channelId to newChannel.id))

                fireStoreInstance.collection(AppConstant.USERS).document(otherUserId)
                    .collection(AppConstant.engagedChannel)
                    .document(currentUserId)
                    .set(mapOf(AppConstant.channelId to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration{
        return chatChannelCollectionRef
            .document(channelId)
            .collection(AppConstant.messages)
            .orderBy(AppConstant.time)
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    Log.e("FIRESTORE", "ChatMessageListener error: ", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                    if(it[AppConstant.type] == MessageType.TEXT)
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    else
                        TODO("Add image message")
                }
                onListen(items)
            }
    }
}