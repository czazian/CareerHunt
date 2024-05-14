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

class SampleAnswer : Fragment() {
    private var isSpinnerInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sample_answer, container, false)

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

        return view
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