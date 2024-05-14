package com.example.careerhunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.careerhunt.data.Interview_FAQ
import com.example.careerhunt.databinding.FragmentSampleAnswerBinding

class SampleAnswer : Fragment() {
    private var _binding: FragmentSampleAnswerBinding? = null
    private val binding get() = _binding!!
    private var isSpinnerInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSampleAnswerBinding.inflate(inflater, container, false)
        arguments?.let {
            val faq = it.getSerializable("selectedFAQ") as Interview_FAQ
            val answer = it.getString("answerText")
            binding.questionsDisplay.text = faq.interviewQuest
            binding.displayAnswer.text = answer
        }
        return binding.root
    }

    private fun setupSpinner() {
        val items = resources.getStringArray(R.array.interview)

        binding.SpinnerDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isSpinnerInitialized) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    when (selectedItem) {
                        items[0] -> replaceFragment(InterviewPrep()) // Navigate to InterviewPrep
                        items[1] -> replaceFragment(InterviewTips())
                        items[2] -> {
                            // Already on SampleAnswer, no action needed
                        }
                    }
                } else {
                    isSpinnerInitialized = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onResume() {
        super.onResume()
        // Reset spinner selection to trigger onItemSelected
        binding.SpinnerDropDown.run {
            setSelection(0) // Set to a different position temporarily
            setSelection(1) // Set to a different position temporarily
            setSelection(2) // Set back to the intended position
        }
        isSpinnerInitialized = false
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frameLayout, fragment)
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leak
    }
}