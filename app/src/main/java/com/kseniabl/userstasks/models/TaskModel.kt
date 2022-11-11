package com.kseniabl.userstasks.models

import com.google.gson.annotations.SerializedName

data class TaskModel (
    @SerializedName("author_name")
    val authorName: String,
    @SerializedName("author_role")
    val authorRole: String,
    @SerializedName("create_date")
    val createDate: String,
    @SerializedName("end_ex_date")
    val endExDate: String,
    @SerializedName("executor_name")
    val executorName: String,
    @SerializedName("is_task_finish")
    var isTaskFinish: Boolean,
    @SerializedName("is_termless")
    val isTermless: Boolean,
    @SerializedName("plan_end_date")
    val planEndDate: String,
    @SerializedName("plan_start_date")
    val planStartDate: String,
    @SerializedName("priority")
    val priority: Int,
    @SerializedName("start_ex_date")
    val startExDate: String,
    @SerializedName("task_description")
    val taskDescription: String,
    @SerializedName("task_id")
    val taskId: String,
    @SerializedName("task_name")
    val taskName: String
)