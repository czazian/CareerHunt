package com.example.careerhunt.dataAdapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.R
import com.example.careerhunt.data.Answer_FAQ
import com.example.careerhunt.data.Interview_FAQ

class PracticeResultsAdapter(
    private var data: List<Pair<Interview_FAQ, Answer_FAQ>>,
    private val context: Context,
    private val editListener: (Answer_FAQ) -> Unit
) : RecyclerView.Adapter<PracticeResultsAdapter.ResultViewHolder>() {
    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtQuestionTitle: TextView = view.findViewById(R.id.QuestionTitle)
        val txtQuestionAnswer: TextView = view.findViewById(R.id.QuestionAns)
        val imgBtnRedo: ImageButton = view.findViewById(R.id.imgBtnRedo)
        val imgBtnCopy: ImageButton = view.findViewById(R.id.imgBtnCopy)
        val txtNoOfQuest: TextView = view.findViewById(R.id.noOfQuest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.practice_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val (question, answer) = data[position]
        holder.txtQuestionTitle.text = question.interviewQuest
        holder.txtNoOfQuest.text = "${position + 1}/${data.size}" // Setting the question number
        holder.txtQuestionAnswer.text = answer.answer

        // Set the click listener for the redo button
        holder.imgBtnRedo.setOnClickListener {
            editListener(answer) // Trigger the edit listener passing the current Answer_FAQ
        }

        // Set the click listener for copying the answer to clipboard
        holder.imgBtnCopy.setOnClickListener {
            copyTextToClipboard(answer.answer)
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<Pair<Interview_FAQ, Answer_FAQ>>) {
        this.data = newData
        notifyDataSetChanged()
    }
}