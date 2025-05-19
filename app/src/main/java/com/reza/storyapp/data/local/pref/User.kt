package com.reza.storyapp.data.local.pref

data class User(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)
