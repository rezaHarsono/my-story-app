package com.reza.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.reza.storyapp.data.remote.pref.User
import com.reza.storyapp.data.remote.pref.UserPreference
import com.reza.storyapp.data.remote.response.FileUploadResponse
import com.reza.storyapp.data.remote.response.ListStoryItem
import com.reza.storyapp.data.remote.response.LoginResponse
import com.reza.storyapp.data.remote.response.RegisterResponse
import com.reza.storyapp.data.remote.response.Story
import com.reza.storyapp.data.remote.response.StoryResponse
import com.reza.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.error == false) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
            Log.e("LoginError", "login: ${errorResponse.message}")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.error == false) {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
            Log.e("RegisterError", "register: ${errorResponse.message}")
        }
    }

    fun getStories(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories()
            if (response.error == false) {
                emit(Result.Success(response.listStory))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
            Log.e("StoryError", "getStories: ${errorResponse.message}")
        }
    }

    fun getStoryById(id: String?): LiveData<Result<Story?>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoryDetail(id)
            if (response.error == false) {
                emit(Result.Success(response.story))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
            Log.e("StoryError", "getStoryById: ${errorResponse.message}")
        }
    }

    suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<FileUploadResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.uploadImage(file, description)
                if (response.error == false) {
                    emit(Result.Success(response))
                } else {
                    emit(Result.Error(response.message.toString()))
                    Log.e("StoryError", "uploadStory: ${response.message}")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
                emit(Result.Error(errorResponse.message.toString()))
            }
        }

    suspend fun saveSession(user: User) {
        userPreference.saveSession(user)
        Log.d("TokenSaved", "TokenSaved: ${user.token}")
    }

    fun getSession(): Flow<User> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }


    companion object {

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ) = UserRepository(apiService, userPreference)
    }
}