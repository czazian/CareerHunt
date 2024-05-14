package com.example.careerhunt.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.*
import com.example.careerhunt.interfaces.QuestionClickCallback

class QuestionsAdapter(private val questions: List<String>, private val callback: QuestionClickCallback) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    // Marking the ViewHolder as inner to access the outer class's members
    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtQuestion: TextView = view.findViewById(R.id.txtQuestion)

        init {
            view.setOnClickListener {
                // Now it can access 'callback' because it's an inner class
                callback.onQuestionClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.txtQuestion.text = question
    }

    override fun getItemCount() = questions.size
}