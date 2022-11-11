package com.kseniabl.userstasks.models

import com.google.gson.annotations.SerializedName

data class InsertTaskModel(
    @SerializedName("executor_name")
    val executorName: String,
    @SerializedName("is_termless")
    val isTermless: Boolean,
    @SerializedName("priority")
    val priority: Int,
    @SerializedName("task_description")
    val taskDescr: String,
    @SerializedName("task_name")
    val taskName: String
)
