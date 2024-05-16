package com.example.careerhunt.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.PracticeInterview
import com.example.careerhunt.R
import com.example.careerhunt.data.Interview_FAQ

class PracticeQuestionsAdapter(
    private var questions: List<Interview_FAQ>,
    private val interaction: PracticeInterview
) : RecyclerView.Adapter<PracticeQuestionsAdapter.QuestionViewHolder>() {

    private val answers = mutableMapOf<String, String>()

    class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtQuestionTitle: TextView = view.findViewById(R.id.QuestionTitle)
        val txtNoOfQuestion: TextView = view.findViewById(R.id.noOfQuest)
        val imgKeyboard: ImageView = view.findViewById(R.id.btnKeyboard)
        val txtKeyboardHint: TextView = view.findViewById(R.id.tvRedo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.practice_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.txtQuestionTitle.text = question.interviewQuest
        holder.txtNoOfQuestion.text = "${position + 1}/${questions.size}"
        holder.txtKeyboardHint.text = answers[question.faqId] ?: "Type your answer here" // Display saved answer if any

        holder.imgKeyboard.setOnClickListener {
            interaction.onKeyboardClick(position) { answer ->
                answers[question.faqId] = answer
                holder.txtKeyboardHint.text = answer
            }
        }
    }

    override fun getItemCount() = questions.size

    fun updateData(newQuestions: List<Interview_FAQ>) {
        this.questions = newQuestions
        notifyDataSetChanged()
    }

    fun areAllAnswersProvided(): Boolean {
        return questions.all { question -> answers.containsKey(question.faqId) && answers[question.faqId]!!.isNotBlank() }
    }

    fun getAnswers(): Map<String, String> = answers
}