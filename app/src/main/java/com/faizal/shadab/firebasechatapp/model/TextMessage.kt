package com.faizal.shadab.firebasechatapp.model

import java.util.*

data class TextMessage(
    override val time: Date,
    override val type: String,
    override val senderId: String,
    val text: String
) : Message{
    constructor(): this(Date(0), "", "", "")
}