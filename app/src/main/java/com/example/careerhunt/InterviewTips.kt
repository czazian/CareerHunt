package com.example.careerhunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit

class InterviewTips : Fragment() {
    private var isSpinnerInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_interview_tips, container, false)

        val spinner: Spinner = view.findViewById(R.id.SpinnerDropDown)
        val items = resources.getStringArray(R.array.interview)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isSpinnerInitialized) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    when (selectedItem) {
                        items[0] -> {
                            replaceFragment(InterviewPrep()) // Go back to InterviewPrep
                        }
                        items[1] -> {
                            // Do nothing, we are already on InterviewTips
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

        setupExpandCollapse(view, R.id.textView5, R.id.imageButton11, R.id.textView12)
        setupExpandCollapse(view, R.id.textView6, R.id.imageButton, R.id.textView6Content)
        setupExpandCollapse(view, R.id.textView7, R.id.imageButton7, R.id.textView7Content)
        setupExpandCollapse(view, R.id.textView9, R.id.imageButton8, R.id.textView9Content)
        setupExpandCollapse(view, R.id.textView10, R.id.imageButton9, R.id.textView10Content)
        setupExpandCollapse(view, R.id.textView11, R.id.imageButton10, R.id.textView11Content)

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = parentFragmentManager
        fragmentManager.commit {
            replace(R.id.frameLayout, fragment)
            addToBackStack(null)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reset spinner selection to trigger onItemSelected
        val spinner: Spinner? = view?.findViewById(R.id.SpinnerDropDown)
        spinner?.setSelection(0) // Set to a different position temporarily
        spinner?.setSelection(1) // Set to a different position temporarily
        isSpinnerInitialized = false
    }

    private fun setupExpandCollapse(view: View, textViewId: Int, buttonId: Int, contentId: Int) {
        val textView = view.findViewById<TextView>(textViewId)
        val button = view.findViewById<ImageButton>(buttonId)
        val content = view.findViewById<TextView>(contentId)

        textView.setOnClickListener {
            toggleVisibility(content, textView, button)
        }

        button.setOnClickListener {
            toggleVisibility(content, textView, button)
        }
    }

    private fun toggleVisibility(content: TextView, textView: TextView, button: ImageButton) {
        if (content.visibility == View.GONE) {
            content.visibility = View.VISIBLE
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.textColorExpanded))
            button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.buttonColorExpanded))
            button.setImageResource(android.R.drawable.arrow_up_float)
        } else {
            content.visibility = View.GONE
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.defaultTextColor))
            button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.defaultTextColor))
            button.setImageResource(android.R.drawable.arrow_down_float)
        }
    }
}