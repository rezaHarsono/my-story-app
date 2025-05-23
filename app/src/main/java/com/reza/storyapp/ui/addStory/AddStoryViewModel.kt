package com.reza.storyapp.ui.addStory

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.reza.storyapp.data.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    private var _uri: MutableLiveData<Uri?> = MutableLiveData<Uri?>()
    val uri get() = _uri

    fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ) =
        repository.uploadStory(file, description, lat, lon)

    fun getUri(): Uri? = uri.value

    fun setUri(uri: Uri?) {
        _uri.value = uri
    }

}