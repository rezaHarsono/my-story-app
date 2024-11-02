package com.reza.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.reza.storyapp.ViewModelFactory
import com.reza.storyapp.ui.login.LoginActivity
import com.reza.storyapp.ui.story.StoryActivity

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, StoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}