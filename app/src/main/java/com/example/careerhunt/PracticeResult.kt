package com.example.careerhunt

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Answer_FAQ
import com.example.careerhunt.data.InterPrac_History
import com.example.careerhunt.data.Interview_FAQ
import com.example.careerhunt.dataAdapter.PracticeResultsAdapter
import com.google.firebase.database.FirebaseDatabase

class PracticeResult : Fragment() {
    private var isSpinnerInitialized = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PracticeResultsAdapter
    private lateinit var sharedIDPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_practice_result, container, false)
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        val btnBackArrow: ImageButton = view.findViewById(R.id.btnBackArrow)
        btnBackArrow.setOnClickListener {
            activity?.supportFragmentManager?.run {
                if (backStackEntryCount > 0) {
                    popBackStack()
                } else {
                    // Handle the case where there's no fragment in the stack
                    activity?.finish()  // or any other handling
                }
            }
        }
        val spinner: Spinner = view.findViewById(R.id.SpinnerDropDown)
        val items = resources.getStringArray(R.array.interview)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isSpinnerInitialized) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    when (selectedItem) {
                        items[0] -> {
                            replaceFragment(InterviewPrep()) // Navigate to InterviewPrep
                        }
                        items[1] -> {
                            replaceFragment(InterviewTips())
                        }
                        items[2] -> {
                            // Do nothing, we are already on PracticeInterview
                        }
                    }
                } else {
                    isSpinnerInitialized = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        val btnPractice: Button = view.findViewById(R.id.btnPractice)
        btnPractice.setOnClickListener {
            replaceFragment(PracticeInterview())
        }

        setupRecyclerView(view)
        setupPracticeButton(view)
        loadUserResults(getCurrentUserId().toInt())

        return view
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewResults)
        adapter = PracticeResultsAdapter(emptyList(), requireContext()) { answerFAQ ->
            showEditDialog(answerFAQ)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun showEditDialog(answerFAQ: Answer_FAQ) {
        val editText = EditText(context).apply {
            setText(answerFAQ.answer)
        }
        AlertDialog.Builder(context)
            .setTitle("Edit Answer")
            .setView(editText)
            .setPositiveButton("Update") { dialog, which ->
                val newAnswer = editText.text.toString()
                updateAnswerInFirebase(answerFAQ.copy(answer = newAnswer))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateAnswerInFirebase(answerFAQ: Answer_FAQ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Answer_FAQ")
        dbRef.child(answerFAQ.faqAnsID).setValue(answerFAQ)
            .addOnSuccessListener {
                Toast.makeText(context, "Answer updated successfully!", Toast.LENGTH_SHORT).show()
                // Refresh data after successful update
                loadUserResults(getCurrentUserId().toInt())
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update answer: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupPracticeButton(view: View) {
        val btnPractice: Button = view.findViewById(R.id.btnPractice)
        btnPractice.setOnClickListener {
            fragmentManager?.commit {
                replace(R.id.frameLayout, PracticeInterview())
                addToBackStack(null)
            }
        }
    }

    private fun loadUserResults(personalID: Int) {
        FirebaseDatabase.getInstance().getReference("InterPrac_History")
            .get().addOnSuccessListener { dataSnapshot ->
                // Filter for the current user's history records directly within the app
                val historyList = dataSnapshot.children.mapNotNull { it.getValue(InterPrac_History::class.java) }
                    .filter { it.personalID == personalID }

                if (historyList.isNotEmpty()) {
                    loadQuestionsAndAnswers(historyList)
                } else {
                    Toast.makeText(context, "No results found for user.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching results: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadQuestionsAndAnswers(historyList: List<InterPrac_History>) {
        val faqAnsIDs = historyList.map { it.faqAnsID }
        FirebaseDatabase.getInstance().getReference("Answer_FAQ")
            .get().addOnSuccessListener { dataSnapshot ->
                val answers = dataSnapshot.children.mapNotNull { it.getValue(Answer_FAQ::class.java) }
                    .filter { faqAnsID -> faqAnsIDs.contains(faqAnsID.faqAnsID) }

                val faqIDs = answers.map { it.faqId }
                FirebaseDatabase.getInstance().getReference("Interview_FAQ")
                    .get().addOnSuccessListener { faqSnapshot ->
                        val faqs = faqSnapshot.children.mapNotNull { it.getValue(Interview_FAQ::class.java) }
                            .filter { faq -> faqIDs.contains(faq.faqId) }

                        val results = historyList.mapNotNull { history ->
                            val answer = answers.find { it.faqAnsID == history.faqAnsID }
                            val faq = faqs.find { it.faqId == answer?.faqId }
                            if (faq != null && answer != null) faq to answer else null
                        }
                        adapter.updateData(results)
                    }
            }.addOnFailureListener {
                Toast.makeText(context, "Error loading answers: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun getCurrentUserId(): String {
        // Retrieve userId from SharedPreferences
        return sharedIDPreferences.getString("userid", "") ?: ""
    }


    override fun onResume() {
        super.onResume()
        // Reset spinner selection to trigger onItemSelected
        val spinner: Spinner? = view?.findViewById(R.id.SpinnerDropDown)
        spinner?.setSelection(0) // Set to a different position temporarily
        spinner?.setSelection(1) // Set to a different position temporarily
        spinner?.setSelection(2) // Set back to the intended position
        isSpinnerInitialized = false
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = parentFragmentManager
        fragmentManager.commit {
            replace(R.id.frameLayout, fragment)
            addToBackStack(null)
        }
    }
}