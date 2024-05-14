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
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.dataAdapter.QuestionsAdapter
import com.example.careerhunt.interfaces.QuestionClickCallback

class InterviewPrep : Fragment(), QuestionClickCallback {

    private var isSpinnerInitialized = false
    private lateinit var questionsRecyclerView: RecyclerView
    private lateinit var questionsAdapter: QuestionsAdapter
    private lateinit var secondMiddleContainer: ConstraintLayout
    private lateinit var categoryButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_interview_prep, container, false)

        val spinner: Spinner = view.findViewById(R.id.SpinnerDropDown)
        val items = resources.getStringArray(R.array.interview)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isSpinnerInitialized) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    when (selectedItem) {
                        items[0] -> {
                            // Already on InterviewPrep
                        }
                        items[1] -> {
                            replaceFragment(InterviewTips())
                        }
                        items[2] -> {
                            replaceFragment(PracticeInterview())
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

        val btnView: Button = view.findViewById(R.id.btnView)
        btnView.setOnClickListener {
            replaceFragment(SampleAnswer())
        }

        categoryButton = view.findViewById(R.id.spinnerCat)
        categoryButton.setOnClickListener { showCategoryDropdown() }

        secondMiddleContainer = view.findViewById(R.id.SecondMiddleContainer)
        questionsRecyclerView = view.findViewById(R.id.questionsRecyclerView)
        questionsRecyclerView.layoutManager = LinearLayoutManager(context)
        questionsAdapter = QuestionsAdapter(listOf(
            "Tell me about yourself.",
            "What is your greatest strength?",
            "Why should we hire you?"
        ), this)
        questionsRecyclerView.adapter = questionsAdapter

        secondMiddleContainer.visibility = View.GONE // Initially hide the second container


        return view
    }

    private fun showCategoryDropdown() {
        val categories = listOf("Category 1", "Category 2", "Category 3", "Category 4", "Category 5", "Category 6", "Category 7", "Category 8", "Category 9", "Category 10", "Category 11")
        val listView = ListView(context).apply {
            adapter = ArrayAdapter<String>(requireContext(), R.layout.item_dropdown, categories)
            setOnItemClickListener { parent, view, position, id ->
                categoryButton.text = categories[position]
            }
        }

        val popupHeight = if (categories.size > 7) 700 else WindowManager.LayoutParams.WRAP_CONTENT // Adjust this value based on your needs

        val popupWindow = PopupWindow(context).apply {
            contentView = listView
            width = categoryButton.width // or LayoutParams.WRAP_CONTENT for full width
            height = popupHeight // set the height or make it dynamic
            isFocusable = true
            elevation = 10.0f

            // Apply rounded background to the popup window
            setBackgroundDrawable(resources.getDrawable(R.drawable.dropdown_item_bg, null))
        }

        // Show the popup below the button
        popupWindow.showAsDropDown(categoryButton, 0, 0)
    }

    override fun onQuestionClicked(question: Int) {
        // Implementation here
        secondMiddleContainer.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        // Reset spinner selection to trigger onItemSelected
        val spinner: Spinner? = view?.findViewById(R.id.SpinnerDropDown)
        spinner?.setSelection(1) // Set to a different position temporarily
        spinner?.setSelection(0) // Set back to the intended position
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