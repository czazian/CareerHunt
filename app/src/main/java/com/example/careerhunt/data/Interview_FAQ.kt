package com.example.careerhunt.data

import java.io.Serializable

data class Interview_FAQ(
    var faqId: String = "",
    var overviewQuest: String = "",
    var interviewQuest: String = "",
    var faqCategory: String = "") : Serializable