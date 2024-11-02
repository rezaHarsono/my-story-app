package com.reza.storyapp.data.remote.pref

data class User(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)
