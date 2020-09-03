package com.faizal.shadab.firebasechatapp.firebaseUtil

import android.util.Log
import com.faizal.shadab.firebasechatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
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
}