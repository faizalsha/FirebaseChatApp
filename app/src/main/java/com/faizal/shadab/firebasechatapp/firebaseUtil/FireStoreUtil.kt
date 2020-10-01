package com.faizal.shadab.firebasechatapp.firebaseUtil

import android.content.Context
import android.util.Log
import com.faizal.shadab.firebasechatapp.model.User
import com.faizal.shadab.firebasechatapp.recyclerview.PersonItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item
import java.lang.NullPointerException

object FireStoreUtil {
    private val fireStoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = fireStoreInstance.document("user/${FirebaseAuth.getInstance().uid
            ?: throw NullPointerException("UID is null")}")

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
        if(name.isNotBlank()) userFieldMap["name"] = name
        if(status.isNotBlank()) userFieldMap["status"] = status
        if(profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User?) -> Unit){
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java))
            }
    }

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return fireStoreInstance.collection("user")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        items.add(PersonItem(it.toObject(User::class.java)!!, context))
                }
                onListen(items)
            }
    }
    //todo : check whether this acutally remove the listener as the listener pass by copy
    fun removeListener(registration: ListenerRegistration) = registration.remove()
}