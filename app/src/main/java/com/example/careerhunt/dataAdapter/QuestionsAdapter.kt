package com.example.careerhunt.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.R
import com.example.careerhunt.data.Interview_FAQ
import com.example.careerhunt.interfaces.QuestionClickCallback

class QuestionsAdapter(private var faqs: List<Interview_FAQ>, private val callback: QuestionClickCallback) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    class QuestionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtQuestion: TextView = view.findViewById(R.id.txtQuestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val faq = faqs[position]
        holder.txtQuestion.text = faq.interviewQuest
        holder.view.setOnClickListener { callback.onQuestionClicked(faq) }
    }

    override fun getItemCount() = faqs.size

    fun updateData(newFaqs: List<Interview_FAQ>) {
        faqs = newFaqs
        notifyDataSetChanged()
    }
}
