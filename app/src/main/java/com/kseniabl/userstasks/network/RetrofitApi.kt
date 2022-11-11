package com.kseniabl.userstasks.network

import com.kseniabl.userstasks.models.*
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApi {

    @POST("login")
    suspend fun login(@Body userLoginModel: UserLoginModel): Response<TokenModel>

    @POST("add-user")
    suspend fun addUser(@Body addUserModel: AddUserModel): Response<Void>

    @GET("tasks")
    suspend fun getTasks(@Header("Authorization") token: String): Response<ArrayList<TaskModel>>

    @POST("task")
    suspend fun insertTask(@Header("Authorization") token: String, @Body insertTaskModel: InsertTaskModel): Response<Void>

    @PATCH("task")
    suspend fun updateTask(@Header("Authorization") token: String, @Body id: IdContainBody): Response<Void>

    @GET("organizations")
    suspend fun getOrganizations(@Header("Authorization") token: String): Response<ArrayList<OrganizationResponse>>

    @POST("report")
    suspend fun getReport(@Header("Authorization") token: String, @Body params: ReportBody): Response<ArrayList<ReportResponse>>
}