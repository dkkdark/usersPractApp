package com.kseniabl.userstasks.network

import com.kseniabl.userstasks.models.*
import javax.inject.Inject

class Repository @Inject constructor(private val retrofitApi: RetrofitApi) {
    suspend fun loginUser(userLoginModel: UserLoginModel) = retrofitApi.login(userLoginModel)
    suspend fun addUser(addUserModel: AddUserModel) = retrofitApi.addUser(addUserModel)
    suspend fun getTasks(token: String) = retrofitApi.getTasks(token)
    suspend fun insertTask(token: String, body: InsertTaskModel) = retrofitApi.insertTask(token, body)
    suspend fun updateTask(token: String, id: IdContainBody) = retrofitApi.updateTask(token, id)
    suspend fun getOrganizations(token: String) = retrofitApi.getOrganizations(token)
    suspend fun getReport(token: String, params: ReportBody) = retrofitApi.getReport(token, params)
}