package com.faizal.shadab.firebasechatapp.recyclerview

import android.content.Context
import com.faizal.shadab.firebasechatapp.R
import com.faizal.shadab.firebasechatapp.model.TextMessage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class TextMessageItem(
    val message: TextMessage,
    val context: Context
): Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getLayout() = R.layout.item_text_message
}