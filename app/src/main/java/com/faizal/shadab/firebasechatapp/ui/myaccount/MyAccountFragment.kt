package com.faizal.shadab.firebasechatapp.ui.myaccount

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.faizal.shadab.firebasechatapp.LoginActivity
import com.faizal.shadab.firebasechatapp.R
import com.faizal.shadab.firebasechatapp.firebaseUtil.FireStoreUtil
import com.faizal.shadab.firebasechatapp.firebaseUtil.FirebaseStorageUtil
import com.faizal.shadab.firebasechatapp.glide.GlideApp
import com.firebase.ui.auth.AuthUI
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_account.view.profile_image
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import java.io.ByteArrayOutputStream

class MyAccountFragment : Fragment() {
    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageByte: ByteArray
    private var pictureJustChanged = false
    //private lateinit var myAccountViewModel: MyAccountViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        myAccountViewModel =
//                ViewModelProviders.of(this).get(MyAccountViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_account, container, false)

        root.apply {
            profile_image.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "select image"), RC_SELECT_IMAGE)
            }

            save_button.setOnClickListener{
                if(::selectedImageByte.isInitialized){
                    FirebaseStorageUtil.uploadProfilePhoto(selectedImageByte){ imagePath ->
                        FireStoreUtil.updateCurrentUser(name_edit_text.editText?.text.toString(),
                                    status_edit_text.editText?.text.toString(), imagePath)
                    }
                }else{
                    FireStoreUtil.updateCurrentUser(name_edit_text.editText?.text.toString(),
                        status_edit_text.editText?.text.toString(), null)
                }
            }

            logout_button.setOnClickListener{
                AuthUI.getInstance()
                    .signOut(context)
                    .addOnCompleteListener {
                        startActivity(intentFor<LoginActivity>().newTask().clearTask())
                    }
            }
        }
        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null){
            val selectedImagePath = data.data
            val selectedImageBitmap = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageByte = outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageByte)
                .into(profile_image)

            pictureJustChanged = true
        }
    }


    override fun onStart() {
        super.onStart()
        FireStoreUtil.getCurrentUser { user ->
            if(this.isVisible && user != null){
                name_edit_text.editText?.setText(user.name)
                status_edit_text.editText?.setText(user.status)
                if(!pictureJustChanged && user.profilePicturePath != null){
                    GlideApp.with(this)
                        .load(FirebaseStorageUtil.pathToReference(user.profilePicturePath))
                        .placeholder(R.drawable.ic_firebase)
                        .into(profile_image)
                }
            }
        }
    }
}
