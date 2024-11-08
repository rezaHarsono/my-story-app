package com.reza.storyapp.ui.regist

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
import com.google.android.material.snackbar.Snackbar
import com.reza.storyapp.ViewModelFactory
import com.reza.storyapp.data.Result
import com.reza.storyapp.databinding.ActivityRegistBinding
import com.reza.storyapp.ui.login.LoginActivity

class RegistActivity : AppCompatActivity() {
    private val registViewModel by viewModels<RegistViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityRegistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        registViewModel.registerResult.observe(this@RegistActivity) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    showSuccessDialog()
                }
                is Result.Error -> {
                    showLoading(false)
                    showSnakebar(result.error)
                }
            }
        }
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
        binding.btnRegist.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val name = binding.edRegisterName.text.toString()

            try {
                registViewModel.register(name, email, password)
            } catch (e: Exception) {
                showSnakebar(e.message.toString())
                Log.e("RegistActivity", "Register failed: ${e.message.toString()}")
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Akun dengan ${binding.edRegisterEmail.text} sudah jadi nih. Yuk, login.")
            setPositiveButton("Lanjut") { _, _ ->
                startActivity(Intent(this@RegistActivity, LoginActivity::class.java))
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
        ObjectAnimator.ofFloat(binding.ivRegist, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcome =
            ObjectAnimator.ofFloat(binding.tvWelcomeRegist, View.ALPHA, 1f).setDuration(1000)
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(1000)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(1000)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(1000)
        val edName =
            ObjectAnimator.ofFloat(binding.nameTextInputLayout, View.ALPHA, 1f).setDuration(1000)
        val edEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val edPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val regist = ObjectAnimator.ofFloat(binding.btnRegist, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            val together = AnimatorSet().apply {
                playTogether(name, email, password, edName, edEmail, edPassword, regist)
            }
            playSequentially(welcome, together)
            start()
        }
    }
}