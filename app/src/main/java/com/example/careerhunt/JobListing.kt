package com.example.careerhunt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
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

            val fragment = AddJob()
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        val container = view.findViewById<ConstraintLayout>(R.id.itemContainer)
        container.setOnClickListener() {
            val fragment = JobDetail()
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

}