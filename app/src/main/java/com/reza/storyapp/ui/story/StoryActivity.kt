package com.reza.storyapp.ui.story

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reza.storyapp.R
import com.reza.storyapp.ViewModelFactory
import com.reza.storyapp.databinding.ActivityStoryBinding
import com.reza.storyapp.ui.addStory.AddStoryActivity
import com.reza.storyapp.ui.login.LoginActivity
import com.reza.storyapp.ui.map.MapsActivity
import com.reza.storyapp.widget.StoryListWidget
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding

    private val storyViewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        setupView()
        setupAction()
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        StoryListWidget.notifyDataSetChanged(context = this@StoryActivity)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_language -> {
                val intent = Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            R.id.action_logout -> {
                lifecycleScope.launch {
                    storyViewModel.logout()
                    StoryListWidget.notifyDataSetChanged(this@StoryActivity)

                    val intent = Intent(this@StoryActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                    finish()
                }
            }

            R.id.action_map -> {
                val intent = Intent(this@StoryActivity, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun setupAction() {
        binding.fab.setOnClickListener {
            val intent = Intent(this@StoryActivity, AddStoryActivity::class.java)
            startActivity(intent)
            onPause()
        }
    }

    private fun setupRecyclerView() {

        val adapter = StoryAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        storyViewModel.getStories().observe(this) { result ->
            adapter.submitData(lifecycle, result)
        }

        binding.rvStories.layoutManager = LinearLayoutManager(this)

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.rvStories.layoutManager?.scrollToPosition(0)
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}