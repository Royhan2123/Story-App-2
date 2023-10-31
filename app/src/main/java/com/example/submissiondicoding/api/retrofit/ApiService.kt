package com.example.submissiondicoding.api.retrofit
import com.example.submissiondicoding.api.response.DetailResponse
import com.example.submissiondicoding.api.response.LoginResponse
import com.example.submissiondicoding.api.response.MapResponse
import com.example.submissiondicoding.api.response.RegisterResponse
import com.example.submissiondicoding.api.response.StoryResponse
import com.example.submissiondicoding.api.response.UploadResponse
import com.example.submissiondicoding.model.LoginModel
import com.example.submissiondicoding.model.RegisterModel

import retrofit2.*
import retrofit2.http.Body
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    suspend fun registerAccount(
        @Body requestBody: RegisterModel,
    ): RegisterResponse

    @POST("login")
    suspend fun loginAccount(
        @Body requestBody: LoginModel,
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(@Header("Authorization") token: String): StoryResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): DetailResponse

    @Multipart
    @POST("stories")
     fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UploadResponse>
    @GET("stories")
    suspend fun getStoryPaging(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): StoryResponse

    @GET("stories?location=1")
    suspend fun getStoriesMap(@Header("Authorization") token: String) : MapResponse
}