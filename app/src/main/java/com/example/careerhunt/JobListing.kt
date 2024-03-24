package com.example.careerhunt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlinx.coroutines.currentCoroutineContext


class JobListing : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_job_listing, container, false)

        val addBtn: ImageButton = view.findViewById(R.id.btnAddJob)
        addBtn.setOnClickListener () {

            val action = JobListingDirections.actionJobListingToAddJob()
            Navigation.findNavController(view).navigate(action)

        }

        return view
    }
}