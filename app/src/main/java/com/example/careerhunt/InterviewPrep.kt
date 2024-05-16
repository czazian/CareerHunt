package com.example.careerhunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Answer_FAQ
import com.example.careerhunt.data.Interview_FAQ
import com.example.careerhunt.dataAdapter.QuestionsAdapter
import com.example.careerhunt.interfaces.QuestionClickCallback
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.careerhunt.databinding.FragmentInterviewPrepBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class InterviewPrep : Fragment(), QuestionClickCallback {

    private lateinit var binding: FragmentInterviewPrepBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRefAnswers: DatabaseReference
    private var selectedFaq: Interview_FAQ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInterviewPrepBinding.inflate(inflater, container, false)

        setupSpinner()
        setupButtonListeners()
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        dbRef = FirebaseDatabase.getInstance().getReference("Interview_FAQ")
        dbRefAnswers = FirebaseDatabase.getInstance().getReference("Answer_FAQ")

        binding.submitbtn.setOnClickListener {
            val overviewQuest = "2 account test"
            val interviewQuest = "2 testing account"
            val faqCat = "Accounting"
            val answerText = "2 account........."
            if (overviewQuest.isNotEmpty() && interviewQuest.isNotEmpty() && answerText.isNotEmpty()) {
                addFAQtoFirebase(overviewQuest, interviewQuest, faqCat, answerText)
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.SecondMiddleContainer.visibility = View.GONE
        binding.spinnerCat.setOnClickListener { showCategoryDropdown() }
        setupRecyclerView()
        setupSpinner()
    }

    private fun addFAQtoFirebase(overviewQuest: String, interviewQuest: String, faqCat: String, answerText: String) {
        val faqId = dbRef.push().key ?: return
        val faq = Interview_FAQ(faqId, overviewQuest, interviewQuest, faqCat)

        dbRef.child(faqId).setValue(faq).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                addAnswerToFirebase(faqId, answerText)
            } else {
                Toast.makeText(context, "Failed to add FAQ: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addAnswerToFirebase(faqId: String, answerText: String) {
        val answerId = dbRefAnswers.push().key ?: return
        val answer = Answer_FAQ(answerId, answerText, faqId)

        dbRefAnswers.child(answerId).setValue(answer).addOnSuccessListener {
            Toast.makeText(context, "FAQ and Answer added successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to add Answer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        binding.questionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = QuestionsAdapter(emptyList(), this@InterviewPrep)
        }
        loadAllInterviewFAQs()
    }

    private fun loadAllInterviewFAQs() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val faqs = snapshot.children.mapNotNull { it.getValue(Interview_FAQ::class.java) }
                (binding.questionsRecyclerView.adapter as QuestionsAdapter).updateData(faqs)
                // Update the questions count to display the total number of questions initially
                binding.questionsCount.text = "${faqs.size} questions"
                // Set the title to "All Questions" when all FAQs are loaded
                binding.titleQuestion.text = "All Questions"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showCategoryDropdown() {
        val categories = listOf(
            "Common Question",
            "Accounting",
            "Administrative",
            "Office Support",
            "Art and Design",
            "Banking and Finance",
            "Customer Service",
            "Construction",
            "Consulting",
            "Education",
            "Engineering",
            "Farming and Animal",
            "Healthcare",
            "Human Resource",
            "IT and Computing",
            "Manufacturing",
            "Marketing",
            "Science",
            "Retail",
            "Sales",
            "Insurance",
            "Hospitality"
        )

        val listView = ListView(context).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
            setOnItemClickListener { _, _, position, _ ->
                val selectedCategory = categories[position]
                binding.spinnerCat.text = selectedCategory
                binding.titleQuestion.text = selectedCategory  // Update the titleQuestion TextView
                filterFAQsByCategory(selectedCategory)
            }
        }

        // Configure the popup window
        val popupHeight = if (categories.size > 7) 700 else WindowManager.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(context).apply {
            contentView = listView
            width = binding.spinnerCat.width
            height = popupHeight
            isFocusable = true
            elevation = 10.0f
            setBackgroundDrawable(resources.getDrawable(R.drawable.dropdown_item_bg, null))
        }

        popupWindow.showAsDropDown(binding.spinnerCat, 0, 0)
    }

    private fun filterFAQsByCategory(category: String) {
        dbRef.orderByChild("faqCategory").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val faqs = snapshot.children.mapNotNull { it.getValue(Interview_FAQ::class.java) }
                    (binding.questionsRecyclerView.adapter as QuestionsAdapter).updateData(faqs)
                    // Update questions count based on the size of the filtered list
                    binding.questionsCount.text = "${faqs.size} questions"
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to filter data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onQuestionClicked(faq: Interview_FAQ) {
        binding.descriptionQuestion.text = faq.overviewQuest
        selectedFaq = faq
        fetchAnswer(faq.faqId) { answer ->
            binding.sampleAnswer.text = answer
            binding.SecondMiddleContainer.visibility = View.VISIBLE
        }
    }

    private fun fetchAnswer(faqId: String, callback: (String) -> Unit) {
        dbRefAnswers.orderByChild("faqId").equalTo(faqId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val answer = snapshot.children.firstOrNull()?.getValue(Answer_FAQ::class.java)?.answer ?: "No answer available"
                    callback(answer)
                    binding.btnView.visibility = View.VISIBLE
                } else {
                    // No answers available, hide the button
                    binding.btnView.visibility = View.GONE
                    callback("No answer available")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch answer: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinner() {
        val items = resources.getStringArray(R.array.interview)
        binding.SpinnerDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                when (selectedItem) {
                    items[0] -> {} // Already on InterviewPrep
                    items[1] -> replaceFragment(InterviewTips())
                    items[2] -> replaceFragment(PracticeInterview())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onResume() {
        super.onResume()
        binding.SpinnerDropDown.setSelection(0) // Reset the spinner selection on resume
    }

    private fun setupButtonListeners() {
        binding.btnView.setOnClickListener {
            selectedFaq?.let { faq ->
                openSampleAnswerFragment(faq)
            } ?: Toast.makeText(context, "No question selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSampleAnswerFragment(faq: Interview_FAQ) {
        val fragment = SampleAnswer.newInstance(faq.faqId)
        replaceFragment(fragment)
    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frameLayout, fragment)
            addToBackStack(null)
        }
    }

}