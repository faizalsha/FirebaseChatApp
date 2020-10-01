package com.faizal.shadab.firebasechatapp.model

import java.util.*

object MessageType{
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {
    val time: Date
    val type: String
    val senderId: String
}