package com.example.careerhunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.careerhunt.data.Answer_FAQ
import com.example.careerhunt.data.InterPrac_History
import com.example.careerhunt.data.Interview_FAQ
import com.example.careerhunt.data.Personal
import com.example.careerhunt.databinding.FragmentSampleAnswerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SampleAnswer : Fragment() {

    private lateinit var binding: FragmentSampleAnswerBinding
    private lateinit var dbRefAnswers: DatabaseReference
    private var answersList: List<Answer_FAQ> = listOf()
    private var currentIndex: Int = 0

    private var isSpinnerInitialized = false
    companion object {
        private const val ARG_FAQ_ID = "faq_id"

        fun newInstance(faqId: String): SampleAnswer {
            val fragment = SampleAnswer()
            val bundle = Bundle().apply {
                putString(ARG_FAQ_ID, faqId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSampleAnswerBinding.inflate(inflater, container, false)
        dbRefAnswers = FirebaseDatabase.getInstance().getReference("Answer_FAQ")

        arguments?.getString(ARG_FAQ_ID)?.let {
            loadAnswersForQuestion(it)
        }

        binding.btnBackArrow.setOnClickListener {
            activity?.supportFragmentManager?.run {
                if (backStackEntryCount > 0) {
                    popBackStack()
                } else {
                    // Handle the case where there's no fragment in the stack
                    activity?.finish()  // or any other handling
                }
            }
        }
        setupNavigationButtons()
        setupSpinner()

        return binding.root
    }

    private fun loadAnswersForQuestion(faqId: String) {
        // First, load the question text
        FirebaseDatabase.getInstance().getReference("Interview_FAQ").child(faqId).get().addOnSuccessListener { dataSnapshot ->
            val faq = dataSnapshot.getValue(Interview_FAQ::class.java)
            binding.questionsDisplay.text = faq?.interviewQuest
        }

        // Then, load answers and their associated personal details
        FirebaseDatabase.getInstance().getReference("Answer_FAQ")
            .orderByChild("faqId").equalTo(faqId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    answersList = snapshot.children.mapNotNull { it.getValue(Answer_FAQ::class.java) }.toList()
                    if (answersList.isNotEmpty()) {
                        currentIndex = 0 // Reset to first answer
                        updateAnswerDisplay()
                        // Load personal details for the first answer
                        loadPersonalDetails(answersList[currentIndex])
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to fetch answers: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadPersonalDetails(answer: Answer_FAQ) {
        FirebaseDatabase.getInstance().getReference("InterPrac_History")
            .orderByChild("faqAnsID").equalTo(answer.faqAnsID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val history = snapshot.children.firstOrNull()?.getValue(InterPrac_History::class.java)
                    if (history != null) {
                        fetchPersonalName(history.personalID, answer)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to fetch user details: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchPersonalName(personalID: Int, answer: Answer_FAQ) {
        FirebaseDatabase.getInstance().getReference("Personal").child(personalID.toString()).get().addOnSuccessListener { dataSnapshot ->
            val person = dataSnapshot.getValue(Personal::class.java)
            updateUI(answer, person)
        }
    }

    private fun updateUI(answer: Answer_FAQ, person: Personal?) {
        binding.displayAnswer.text = answer.answer
        binding.tvPersonal.text = person?.name ?: "Unknown"
    }

    private fun setupNavigationButtons() {
        binding.nextArrorButton.setOnClickListener {
            if (currentIndex < answersList.size - 1) {
                currentIndex++
                updateAnswerDisplay()
            }
        }

        binding.backArrorbutton.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateAnswerDisplay()
            }
        }
    }

    private fun updateAnswerDisplay() {
        binding.displayAnswer.text = answersList[currentIndex].answer
        // Load personal details for the current answer
        loadPersonalDetails(answersList[currentIndex])
        binding.QuestionTitle.text = "Sample Answer ${currentIndex + 1}"
    }

    private fun setupSpinner() {
        val items = resources.getStringArray(R.array.interview)
        binding.SpinnerDropDown.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)

        // Initial setup without preventing the first selection from navigating
        binding.SpinnerDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isSpinnerInitialized) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    navigateToSelectedFragment(selectedItem)
                } else {
                    isSpinnerInitialized = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun navigateToSelectedFragment(selectedItem: String) {
        when (selectedItem) {
            "Interview Preparation" -> replaceFragment(InterviewPrep())
            "Interview Tips" -> replaceFragment(InterviewTips())
            "Practice Interview" -> replaceFragment(PracticeInterview())
        }
    }

    override fun onResume() {
        super.onResume()
        // Avoid spinner reselection here unless absolutely necessary
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frameLayout, fragment)
            addToBackStack(null)
        }
    }

}