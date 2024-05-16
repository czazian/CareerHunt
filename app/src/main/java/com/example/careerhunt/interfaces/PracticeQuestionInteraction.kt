package com.example.careerhunt.interfaces

interface PracticeQuestionInteraction {
    fun onKeyboardClick(position: Int, callback: (String) -> Unit)
}