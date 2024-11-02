package com.reza.storyapp.ui.storyDetail

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.reza.storyapp.ViewModelFactory
import com.reza.storyapp.data.Result
import com.reza.storyapp.databinding.ActivityDetailBinding
import com.reza.storyapp.ui.story.StoryAdapter.Companion.STORY_ID_KEY

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val detailViewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupContent()
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
    }

    private fun setupContent() {
        val storyId = intent.getStringExtra(STORY_ID_KEY)
        detailViewModel.getStoryById(storyId).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    val story = result.data
                    binding.apply {
                        Glide.with(this@DetailActivity)
                            .load(story?.photoUrl)
                            .into(ivDetailPhoto)
                        tvDetailName.text = story?.name
                        tvDetailDescription.text = story?.description
                    }
                    supportActionBar?.title = story?.name
                }

                is Result.Error -> {
                    showLoading(false)
                    showSnackbar(result.error)
                    Log.e("DetailError", "setupContent: ${result.error}")
                }

                is Result.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}