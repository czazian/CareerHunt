package com.example.careerhunt.interfaces

import com.example.careerhunt.data.Interview_FAQ

interface QuestionClickCallback {
    fun onQuestionClicked(faq: Interview_FAQ)
}
