package dev.rushia.verhaal_mobile.data.remote.retrofit

import dev.rushia.verhaal_mobile.data.remote.response.ListStoriesResponse
import dev.rushia.verhaal_mobile.data.remote.response.LoginResponse
import dev.rushia.verhaal_mobile.data.remote.response.RegisterResponse
import dev.rushia.verhaal_mobile.data.remote.response.StoryResponse
import dev.rushia.verhaal_mobile.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getStoriesList(
        @Header("Authorization") token: String
    ): Call<ListStoriesResponse>

    @POST("stories")
    @Multipart
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UploadResponse>

    @GET("stories/{id}")
    fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<StoryResponse>
}