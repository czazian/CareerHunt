package com.example.careerhunt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerhunt.dataAdapter.JobListingAdapter
import com.example.careerhunt.databinding.FragmentJobListingBinding
import com.example.careerhunt.viewModel.CompanyViewModel
import com.example.careerhunt.viewModel.JobViewModel
import com.example.careerhunt.viewModel.JobRepository


class JobListing : Fragment(), JobViewModel.RecyclerViewEvent {

    private lateinit var binding: FragmentJobListingBinding
    private lateinit var jobViewModel : JobViewModel
    private lateinit var companyViewModel : CompanyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobListingBinding.inflate(layoutInflater, container, false)


        //Hardcoded company ID
        val companyID = 1


        binding.recommendedJobRecyclerView.setNestedScrollingEnabled(false);
        binding.newJobRecyclerView.setNestedScrollingEnabled(false);

        //Get View Model
        jobViewModel = ViewModelProvider(this).get(JobViewModel::class.java)
        companyViewModel = ViewModelProvider(this).get(CompanyViewModel::class.java)

        //Go to add job
        binding.btnAddJob.setOnClickListener() {
            val fragment = AddJob()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }


        //Display Recommended Job -> Need to pass in the viewModel and viewLifeCycleOwner in order to get data
        val recommendedJobAdapter = JobListingAdapter(this, companyViewModel, viewLifecycleOwner)

        binding.recommendedJobRecyclerView.adapter = recommendedJobAdapter
        binding.recommendedJobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recommendedJobRecyclerView.setHasFixedSize(true)

        jobViewModel.readRecommendedJob().observe(viewLifecycleOwner, Observer { jobList ->
            recommendedJobAdapter.setData(jobList)
        });


        //Display New Job
        //Use the Foreign Key (from Job) to retrieve records (from Company) -> Need to pass in the viewModel and viewLifeCycleOwner in order to get data
        val newJobAdapter = JobListingAdapter(this, companyViewModel, viewLifecycleOwner)

        binding.newJobRecyclerView.adapter = newJobAdapter
        binding.newJobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newJobRecyclerView.setHasFixedSize(true)

        jobViewModel.readNewJob().observe(viewLifecycleOwner, Observer { jobList ->
            newJobAdapter.setData(jobList)
        });



        //return view
        return binding.root
    }


    //On each RecyclerView Item clicked - Perform actual action here
    override fun onItemClick(position: Int) {
        jobViewModel.readNewJob().observe(viewLifecycleOwner, Observer { jobList ->

            val fragment = JobDetail()

            val bundle = Bundle()
            bundle.putString("jobID", jobList[position].jobID.toString())
            fragment.arguments = bundle

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()

        })
    }

}