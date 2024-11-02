package com.reza.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.reza.storyapp.ViewModelFactory
import com.reza.storyapp.data.Result
import com.reza.storyapp.data.remote.pref.User
import com.reza.storyapp.databinding.ActivityLoginBinding
import com.reza.storyapp.ui.regist.RegistActivity
import com.reza.storyapp.ui.story.StoryActivity
import com.reza.storyapp.widget.StoryListWidget
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnRegistNow.setOnClickListener {
            val intent = Intent(this, RegistActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            try {
                lifecycleScope.launch {
                    loginViewModel.login(email, password).observe(this@LoginActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }

                            is Result.Success -> {
                                showLoading(false)
                                val token = result.data.loginResult?.token
                                val user = User(email, token ?: "")
                                lifecycleScope.launch {
                                    loginViewModel.saveSession(user)
                                }
                                StoryListWidget.notifyDataSetChanged(this@LoginActivity)
                                Log.d("Token", "Token: $token")
                                showSuccessDialog()
                            }

                            is Result.Error -> {
                                showLoading(false)
                                showSnakebar(result.error)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                showSnakebar(e.message.toString())
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessDialog() {

        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Anda sudah berhasil login.")
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(this@LoginActivity, StoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showSnakebar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcome =
            ObjectAnimator.ofFloat(binding.tvWelcomeLogin, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val edEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val edPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val registText = ObjectAnimator.ofFloat(binding.tvRegist, View.ALPHA, 1f).setDuration(500)
        val regist = ObjectAnimator.ofFloat(binding.btnRegistNow, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            val together = AnimatorSet().apply {
                playTogether(email, password, edEmail, edPassword, login, registText, regist)
            }
            playSequentially(welcome, together)
            start()
        }

    }
}