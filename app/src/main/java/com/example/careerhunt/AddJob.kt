package com.example.careerhunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton

class AddJob : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_job, container, false)

        val backButton: ImageButton = view.findViewById(R.id.btnCancel)

        backButton.setOnClickListener() {
            val fragment = JobListing()
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }
}