package com.example.careerhunt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerhunt.data.Job
import com.example.careerhunt.dataAdapter.JobListingAdapter
import com.example.careerhunt.databinding.FragmentJobListingBinding
import com.example.careerhunt.interfaces.JobInterface
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class JobListing : Fragment(), JobInterface.RecyclerViewEvent {

    private lateinit var binding: FragmentJobListingBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var newJobList: ArrayList<Job>
    private lateinit var recommendedJobList: ArrayList<Job>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobListingBinding.inflate(layoutInflater, container, false)


        //Hardcoded company ID
        //val companyID = 1

        binding.recommendedJobRecyclerView.setNestedScrollingEnabled(false);
        binding.newJobRecyclerView.setNestedScrollingEnabled(false);

        //Go to add job
        binding.btnAddJob.setOnClickListener() {
            val fragment = AddJob()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        //Data Handling
        fetchRecommendedJobData()
        fetchNewJobData()


        //View more - all
        binding.btnShowAllLatest.setOnClickListener(){
            val fragment = SearchJob()
            val bundle = Bundle()
            bundle.putString("from", "latest")
            fragment.arguments = bundle

            val navigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            navigationView.selectedItemId = R.id.explore

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        //View more - recommended
        binding.btnShowAllRecommended.setOnClickListener(){
            val fragment = SearchJob()
            val bundle = Bundle()
            bundle.putString("from", "recommend")
            fragment.arguments = bundle

            val navigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            navigationView.selectedItemId = R.id.explore

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }


        //return view
        return binding.root
    }


    private fun fetchNewJobData() {
        //Get the listener
        val listerner = this

        dbRef = FirebaseDatabase.getInstance().getReference("Job")
        val query = dbRef.orderByChild("jobPostedDate").limitToLast(5) //Make a filter on the result from firebase

        newJobList = arrayListOf()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newJobList.clear()
                if (snapshot.exists()) {
                    for (jobSnapshot in snapshot.children.reversed()) {
                        val job = jobSnapshot.getValue(Job::class.java)
                        newJobList.add(job!!)
                    }
                    val adapter = JobListingAdapter(listerner)
                    adapter.setData(newJobList)
                    binding.newJobRecyclerView.adapter = adapter
                    binding.newJobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.newJobRecyclerView.setHasFixedSize(true)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun fetchRecommendedJobData() {

        //Get the listener
        val listerner = this





        //Get All Job related to User Job
        dbRef = FirebaseDatabase.getInstance().getReference("Job")
        val query = dbRef.orderByChild("jobCategory").equalTo("IT and Computing").limitToLast(5) //Later need to change to user job category

        recommendedJobList = arrayListOf()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recommendedJobList.clear()
                if (snapshot.exists()) {
                    for (jobSnapshot in snapshot.children.reversed()) {
                        val job = jobSnapshot.getValue(Job::class.java)
                        recommendedJobList.add(job!!)
                    }
                    val adapter = JobListingAdapter(listerner)
                    adapter.setData(recommendedJobList)
                    binding.recommendedJobRecyclerView.adapter = adapter
                    binding.recommendedJobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.recommendedJobRecyclerView.setHasFixedSize(true)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }


    //On each RecyclerView Item clicked - Perform actual action here
    override fun onItemClick(position: Int) {
        //Get the clicked object
        val jobObj: Job = newJobList[position]
        val fragment = JobDetail()

        //Add Result Object into Bundle
        val bundle = Bundle()
        bundle.putSerializable("job", jobObj)
        fragment.arguments = bundle

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }



}