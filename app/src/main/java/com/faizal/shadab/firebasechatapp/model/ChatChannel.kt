package com.faizal.shadab.firebasechatapp.model

class ChatChannel(val usersId: MutableList<String>) {
    constructor(): this(mutableListOf())
}