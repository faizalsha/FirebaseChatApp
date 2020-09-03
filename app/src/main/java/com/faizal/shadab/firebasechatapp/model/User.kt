package com.faizal.shadab.firebasechatapp.model

data class User(val name: String,
                val status: String,
                val profilePicturePath: String?) {
    constructor(): this("", "", null)
}