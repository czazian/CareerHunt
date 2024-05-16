package com.example.careerhunt

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Answer_FAQ
import com.example.careerhunt.data.InterPrac_History
import com.example.careerhunt.data.Interview_FAQ
import com.example.careerhunt.dataAdapter.PracticeQuestionsAdapter
import com.example.careerhunt.interfaces.PracticeQuestionInteraction
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PracticeInterview : Fragment(), PracticeQuestionInteraction {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PracticeQuestionsAdapter
    private var isSpinnerInitialized = false
    private lateinit var categorySpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_practice_interview, container, false)
        setupSpinners(view)
        setupRecyclerView(view)
        setupEndButton(view)
        return view
    }

    private fun setupSpinners(view: View) {
        // Setup main category spinner for job categories
        categorySpinner = view.findViewById(R.id.practiceSpnCat)
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                fetchQuestions(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                fetchQuestions("Common Question") // Default category or handle appropriately
            }
        }

        // Setup another spinner for navigation if exists
        val spinner: Spinner = view.findViewById(R.id.SpinnerDropDown)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                handleNavigationSpinner(parent, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun handleNavigationSpinner(parent: AdapterView<*>, position: Int) {
        when (parent.getItemAtPosition(position).toString()) {
            "FAQ" -> replaceFragment(InterviewPrep())
            "Interview Tips" -> replaceFragment(InterviewTips())
            // Other cases or default
        }
    }

    private fun fetchQuestions(category: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Interview_FAQ")
        dbRef.orderByChild("faqCategory").equalTo(category).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val questions = mutableListOf<Interview_FAQ>()
                snapshot.children.mapNotNullTo(questions) { dataSnapshot ->
                    dataSnapshot.getValue(Interview_FAQ::class.java)?.apply {
                        faqId = dataSnapshot.key ?: "" // assuming you have a faqId attribute in Interview_FAQ
                    }
                }
                if (questions.isEmpty()) {
                    Log.d("PracticeInterview", "No questions found for category: $category")
                } else {
                    Log.d("PracticeInterview", "Loaded ${questions.size} questions for category: $category")
                }
                adapter.updateData(questions)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PracticeInterview", "Failed to load data: ${error.message}")
            }
        })
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewPracticeQuestions)
        adapter = PracticeQuestionsAdapter(emptyList(), this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun setupEndButton(view: View) {
        val btnEnd: Button = view.findViewById(R.id.btnEnd)
        btnEnd.setOnClickListener {
            if (adapter.areAllAnswersProvided()) {
                storeAnswersAndProceed()
            } else {
                Toast.makeText(context, "Please answer all questions before ending the interview.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun storeAnswersAndProceed() {
        val answerMap = adapter.getAnswers()
        if (answerMap.isEmpty() || !adapter.areAllAnswersProvided()) {
            Toast.makeText(context, "Please answer all questions before ending the interview.", Toast.LENGTH_LONG).show()
            return
        }

        val answerRef = FirebaseDatabase.getInstance().getReference("Answer_FAQ")
        val historyRef = FirebaseDatabase.getInstance().getReference("InterPrac_History")

        answerMap.forEach { (faqId, answer) ->
            val faqAnsID = answerRef.push().key ?: ""
            if (faqAnsID.isBlank()) {
                Toast.makeText(context, "Error generating unique ID for the answers.", Toast.LENGTH_SHORT).show()
                return@forEach
            }
            val answerFAQ = Answer_FAQ(faqAnsID, answer, faqId)
            answerRef.child(faqAnsID).setValue(answerFAQ).addOnSuccessListener {
                historyRef.push().setValue(InterPrac_History(faqAnsID, getCurrentUserId()))
                Toast.makeText(context, "Answer saved successfully.", Toast.LENGTH_SHORT).show()
                replaceFragment(PracticeResult())
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to save answer: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun getCurrentUserId(): Int {
        // Implement logic to retrieve the current user's ID
        return 1  // Example ID
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frameLayout, fragment)
            addToBackStack(null)
        }
    }
    override fun onKeyboardClick(position: Int, callback: (String) -> Unit) {
        // Use an AlertDialog for input
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        AlertDialog.Builder(context)
            .setTitle("Enter your answer")
            .setView(input)
            .setPositiveButton("Submit") { dialog, which ->
                callback(input.text.toString())
            }
            .setNegativeButton("Cancel", { dialog, which -> dialog.cancel() })
            .show()
    }
    override fun onResume() {
        super.onResume()
        val spinner: Spinner? = view?.findViewById(R.id.SpinnerDropDown)
        spinner?.setSelection(2) // Reset to PracticeInterview
        categorySpinner.setSelection(0, true)  // Force an initial selection if needed
        isSpinnerInitialized = false
    }
}