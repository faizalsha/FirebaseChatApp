package com.faizal.shadab.firebasechatapp.recyclerview

import android.content.Context
import com.faizal.shadab.firebasechatapp.R
import com.faizal.shadab.firebasechatapp.firebaseUtil.FirebaseStorageUtil
import com.faizal.shadab.firebasechatapp.glide.GlideApp
import com.faizal.shadab.firebasechatapp.model.User
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.view.*

class PersonItem(
    private val person: User,
    private val context: Context
): Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            textView_name.text = person.name
            textView_status.text = person.status
        }
        if(person.profilePicturePath != null)
            GlideApp.with(context)
                .load(FirebaseStorageUtil.pathToReference(person.profilePicturePath))
                .placeholder(R.drawable.ic_people)
                .into(viewHolder.itemView.imageView_profile_picture)
    }

    override fun getLayout() = R.layout.item_person
}