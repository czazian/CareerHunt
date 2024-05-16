package com.example.careerhunt.data

import java.io.Serializable

data class Apply_Job(
    var apply_jobID: Long? = 0,
    var jobID: String? = "",
    var personalID: Long? = 0
) : Serializable

