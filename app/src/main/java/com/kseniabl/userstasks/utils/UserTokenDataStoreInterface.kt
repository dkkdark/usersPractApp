package com.kseniabl.userstasks.utils

import kotlinx.coroutines.flow.Flow

interface UserTokenDataStoreInterface {
    val readToken: Flow<String>
    suspend fun saveToken(token: String)
}