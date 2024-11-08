package com.reza.storyapp.ui.addStory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.reza.storyapp.R
import com.reza.storyapp.ViewModelFactory
import com.reza.storyapp.data.Result
import com.reza.storyapp.databinding.ActivityAddStoryBinding
import com.reza.storyapp.getImageUri
import com.reza.storyapp.reduceFileImage
import com.reza.storyapp.ui.story.StoryActivity
import com.reza.storyapp.uriToFile
import com.reza.storyapp.widget.StoryListWidget
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        addStoryViewModel.uri.observe(this) { uri ->
            if (uri != null) {
                binding.ivAddStoryImage.setImageURI(uri)
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        supportActionBar?.title = applicationContext.getString(R.string.add_story)
    }

    private fun setupAction() {
        with(binding) {
            btnCamera.setOnClickListener {
                startCamera()
            }
            btnGallery.setOnClickListener {
                startGallery()
            }
            btnAdd.setOnClickListener {
                uploadStory()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            addStoryViewModel.setUri(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun startCamera() {
        addStoryViewModel.setUri(getImageUri(this))
        launcherIntentCamera.launch(addStoryViewModel.getUri()!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            addStoryViewModel.setUri(addStoryViewModel.getUri())
        }
        if (!isSuccess) {
            addStoryViewModel.setUri(null)
        }
    }

    private fun checkLocationPermissionAndGetLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnCompleteListener(this) {
                val location: Location? = it.result
                callback(location)
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadStory() {
        val includeLocation = binding.switchLocation.isChecked

        if (includeLocation) {
            checkLocationPermissionAndGetLocation { location ->
                if (location != null) {
                    handleUploadStory(location.latitude.toFloat(), location.longitude.toFloat())
                } else {
                    showSnackbar(ContextCompat.getString(this, R.string.location_not_found))
                }
            }
        } else {
            handleUploadStory()
        }
        StoryListWidget.notifyDataSetChanged(this@AddStoryActivity)
    }

    private fun handleUploadStory(lat: Float? = null, long: Float? = null) {
        addStoryViewModel.uri.observe(this) { uri ->
            uri?.let {
                val description = binding.edAddDescription.text.toString()
                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val imageFile = uriToFile(uri, this).reduceFileImage()
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile,
                )
                lifecycleScope.launch {
                    addStoryViewModel.uploadStory(multipartBody, requestBody, lat, long)
                        .observe(this@AddStoryActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Success -> {
                                        showLoading(false)
                                        showSnackbar(getString(R.string.uploadSuccess))
                                        val intent = Intent(this@AddStoryActivity, StoryActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }

                                    is Result.Error -> {
                                        showLoading(false)
                                        showSnackbar(result.error)
                                    }

                                    is Result.Loading -> {
                                        showLoading(true)
                                    }
                                }
                            }
                        }
                }
                Log.d("Image File", "Image File: ${imageFile.path}")
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