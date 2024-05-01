package com.example.careerhunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.careerhunt.databinding.FragmentJobDetailBinding
import com.example.careerhunt.databinding.FragmentJobListingBinding
import com.example.careerhunt.viewModel.CompanyViewModel
import com.example.careerhunt.viewModel.JobViewModel

class JobDetail : Fragment() {

    private lateinit var binding: FragmentJobDetailBinding
    private lateinit var jobViewModel : JobViewModel
    private lateinit var companyViewModel: CompanyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobDetailBinding.inflate(layoutInflater, container, false)

        //Get View Model
        jobViewModel = ViewModelProvider(this).get(JobViewModel::class.java)
        companyViewModel = ViewModelProvider(this).get(CompanyViewModel::class.java)

        val jobID = arguments?.getString("jobID")


        jobViewModel.readJob(jobID.toString().toInt()).observe(viewLifecycleOwner, Observer { jobList ->
            binding.jobCat.text = jobList.jobCategory.toString()
            binding.jobTy.text = jobList.jobType.toString()
            binding.jobTi.text = jobList.jobName.toString()
            binding.jobLoca.text = jobList.jobLocation.toString()
            binding.lblSalaryResult.text = jobList.jobSalary.toString()
            binding.lvlPostDateResult.text = jobList.jobPostedDate.toString()
            binding.descResult.text = jobList.jobDescription

            //Use the Foreign Key (from Job) to retrieve records (from Company)
            var companyID:Int = jobList.companyID
            companyViewModel.readCompany(companyID.toString().toInt()).observe(viewLifecycleOwner, Observer { aCompany ->
                binding.comName.text = aCompany.compName.toString()
            })
        })


        //Back Button
        binding.btnBack.setOnClickListener() {
            val fragment = JobListing()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }


        return binding.root
    }
}