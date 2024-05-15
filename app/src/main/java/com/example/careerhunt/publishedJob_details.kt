package com.example.careerhunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.careerhunt.data.Job


class publishedJob_details : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_published_job_details, container, false)

        // Retrieve the Job object from arguments
        val job: Job? = arguments?.getSerializable("job") as? Job

        val tvJobName : TextView = view.findViewById(R.id.tvJobName)
        val tVJobCat : TextView = view.findViewById(R.id.tVJobCat)
        val jobType : TextView = view.findViewById(R.id.jobType)
        val jobState : TextView = view.findViewById(R.id.jobState)
        val jobLoca : TextView = view.findViewById(R.id.jobLoca)
        val lblSalaryResult : TextView = view.findViewById(R.id.lblSalaryResult)
        val lblPostDateResult : TextView = view.findViewById(R.id.lblPostDateResult)
        val descResult : TextView = view.findViewById(R.id.descResult)
        val btnViewApplicant : Button = view.findViewById(R.id.btnViewApplicant)
        val btnBack : ImageButton = view.findViewById(R.id.btnBack)


        // assign all the job data to each view
        tvJobName.text = job?.jobName.toString()
        tVJobCat.text = job?.jobCategory.toString()
        jobType.text = job?.jobType.toString()
        jobState.text = job?.jobLocationState.toString()
        jobLoca.text = job?.jobLocationCity.toString()
        lblSalaryResult.text = job?.jobSalary.toString()
        lblPostDateResult.text = job?.jobPostedDate.toString()
        descResult.text = job?.jobDescription.toString()

        btnViewApplicant.setOnClickListener() {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            // passing the job data to ViewApplicant fragment
            val newFragment = ViewApplicant()
            val bundle = Bundle()
            bundle.putSerializable("job", job)
            newFragment.arguments = bundle

            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        return view
    }

}