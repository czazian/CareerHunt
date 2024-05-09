package com.example.careerhunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InterviewPrep : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_interview_prep, container, false)

        // Find the Spinner
        val spinnerOption: Spinner = view.findViewById(R.id.btnMoreVert)

        // Define the data source
        val itemsOption = arrayOf("FAQ", "Interview Tips", "Practice Interview")

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapterOption = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsOption)

        // Specify the layout to use when the list of choices appears
        adapterOption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinnerOption.adapter = adapterOption

        // Set item click listener
        spinnerOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle item selection here
                val selectedItem = itemsOption[position]
                // You can perform actions based on the selected item
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing here
            }
        }



        return view
    }

}