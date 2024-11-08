package com.reza.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.reza.storyapp.data.local.StoryEntity
import com.reza.storyapp.data.local.StoryRoomDatabase
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
    private val storyDatabase: StoryRoomDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            if (response.error == false) {
                Result.Success(response)
            } else {
                Result.Error(response.message.toString())
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            Result.Error(errorResponse.message.toString())
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            if (response.error == false) {
                Result.Success(response)
            } else {
                Result.Error(response.message.toString())
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            Log.e("RegisterError", "register: ${errorResponse.message}")
            Result.Error(errorResponse.message.toString())
        } catch (e: Exception) {
            Result.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }

    fun getStories(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData
    }

    fun getStoriesWithId(id: String): LiveData<Result<Story?>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithId(id)
            if (response.error == false) {
                emit(Result.Success(response.story))
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

    fun getStoriesWithLocation(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
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

    suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float? = null,
        lon: Float? = null
    ): LiveData<Result<FileUploadResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.uploadImage(file, description, lat, lon)
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
            storyDatabase: StoryRoomDatabase,
            apiService: ApiService,
            userPreference: UserPreference,
        ) = UserRepository(storyDatabase ,apiService, userPreference)
    }
}