package com.example.submissiondicoding.api
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submissiondicoding.api.response.LoginResponse
import com.example.submissiondicoding.api.response.LoginResult
import com.example.submissiondicoding.api.response.MapItem
import com.example.submissiondicoding.api.response.RegisterResponse
import com.example.submissiondicoding.api.response.StoryDetail
import com.example.submissiondicoding.api.response.StoryItem
import com.example.submissiondicoding.api.response.UploadResponse
import com.example.submissiondicoding.api.retrofit.ApiService
import com.example.submissiondicoding.model.LoginModel
import com.example.submissiondicoding.model.RegisterModel
import com.example.submissiondicoding.preferences.UserPreference
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import com.example.submissiondicoding.api.Result


class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {

    private lateinit var token: String

    suspend fun registerAccount(name: String, email: String, password: String): RegisterResponse {
        return try {
            val requestBody = RegisterModel(name, email, password)
            val response = apiService.registerAccount(requestBody)
            if (response.error) {
                RegisterResponse(true, response.message)
            } else {
                RegisterResponse(false, response.message)
            }
        } catch (e: Exception) {
            RegisterResponse(true, e.message ?: "Failed to register account")
        }
    }


    suspend fun loginAccount(email: String, password: String): LoginResponse {
        Result.Loading
        return try {
            val requestBody = LoginModel(email, password)
            val response = apiService.loginAccount(requestBody)

            if (response.error) {
                LoginResponse(LoginResult(token = ""), true, response.message)

            } else {
                token = response.loginResult.token
                userPreference.saveToken(token)
                userPreference.login()

                Result.Success(response.message)
                LoginResponse(response.loginResult, false, response.message)
            }
        } catch (e: Exception) {
            LoginResponse(LoginResult(token = ""), true, e.message ?: "Failed to login")
        }
    }

    fun getStoryPaging(token: String): LiveData<PagingData<StoryItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource("Bearer $token", apiService)
            }
        ).liveData
    }

    fun getDetailStory(token: String, id: String): LiveData<Result<StoryDetail>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoryDetail("Bearer $token", id)
            val storyItem = response.story
            if (storyItem != null) {
                emit(Result.Success(storyItem))
            } else {
                emit(Result.Error("Failed to retrieve story detail"))
            }
        } catch (e: Exception) {
            emit(Result.Error("An error occurred"))
        }
    }

    fun uploadStory(token: String, description: String, photo: File) {
        val requestPhotoFile = photo.asRequestBody("image/jpg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            requestPhotoFile
        )
        val photoDescription = description.toRequestBody("text/plain".toMediaType())
        val uploadImageRequest = apiService.uploadStory("Bearer $token", imageMultipart, photoDescription)

        uploadImageRequest.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>,
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        Result.Success(responseBody.message)
                    }
                } else {
                    Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Result.Error(t.message ?: "Failed to upload story")
            }

        })
    }

    fun getMapStory(token: String): LiveData<Result<List<MapItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesMap("Bearer $token")
            val maps = response.listStory
            val mapList = maps?.map { story ->
                MapItem(
                    photoUrl = story?.photoUrl,
                    createdAt = story?.createdAt,
                    name = story?.name,
                    description = story?.description,
                    lon = story?.lon,
                    id = story?.id,
                    lat = story?.lat
                )
            }
            Log.d("StoryRepository", "getStoriesMap: Panjang list ${mapList?.size} ")
            emit(Result.Success(mapList ?: emptyList()))
        } catch (e: Exception) {
            Log.d("StoryRepository", "getStoriesMap: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}