package com.example.careerhunt

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Job
import com.example.careerhunt.dataAdapter.PublishedJobListAdapter
import com.example.careerhunt.databinding.FragmentViewPublishedJobBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewPublishedJob : Fragment() {
    private lateinit var binding: FragmentViewPublishedJobBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedIDPreferences: SharedPreferences
    private lateinit var myRef: DatabaseReference
    private lateinit var publishedJobList: ArrayList<Job>
    private var userId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_published_job, container, false)

        // Access SharedPreferences from MainActivity
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        // Retrieve userId from SharedPreferences
        userId = sharedIDPreferences.getString("userid","") ?: ""
        //val userIdString = sharedIDPreferences.getString("userid", "")
       //val userId = userIdString?.toIntOrNull() ?: 0 // Default value is 0 if userIdString is null or not convertible to Int



        // Initialize RecycleView
        recyclerView = view.findViewById(R.id.rvPublishedJob)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager here
        recyclerView.setHasFixedSize(true)

        // Fetch data from Firebase
        fetchData()

       /* view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener() {
            requireActivity().onBackPressed()
        }*/
/*
        // Initialize RecycleView
        recyclerView = view.findViewById(R.id.rvPublishedJob)
        publishedJobList = arrayListOf<Job>()
        //fetchPublishedJobData()
        fetchData()

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext()) // Set layout manager here
        recyclerView.setHasFixedSize(true)
*/




        //profilePicImageView = binding.profilePic as CircleImageView


        /* binding.btnClick.setOnClickListener(){
            // Navigate to View applicant fragment
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = ViewApplicant()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }*/

        return view
    }

    private fun fetchData(){

        // Firebase connection
        myRef = FirebaseDatabase.getInstance().getReference("Job")
        publishedJobList = arrayListOf()

        Toast.makeText(requireContext(), "start fetch", Toast.LENGTH_SHORT).show()
        val query = myRef.orderByChild("companyID").equalTo(userId.toDouble())

        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TAG", "onDataChange")

                publishedJobList.clear()
                Log.d("TAG", snapshot.children.toString())
                if(snapshot.exists()) {
                    Log.d("TAG", "SNAPSHOT EXIST")
                    for (publishedSnap in snapshot.children) {
                        Log.d(TAG, "RetrieveValue")

                        val publishedJob = publishedSnap.getValue(Job::class.java)
                        publishedJobList.add(publishedJob!!)
                    }
                    val adapter = PublishedJobListAdapter()
                    adapter.setData(publishedJobList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()

                }else{
                    Toast.makeText(requireContext(), "noSnap", Toast.LENGTH_SHORT).show()

                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    /*fun onViewApplicantClick(view: View){
        // Navigate to View applicant fragment
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val newFragment = ViewApplicant()
        fragmentTransaction.replace(R.id.frameLayout, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }*/

    /*
    private fun fetchPublishedJobData() {
        Toast.makeText(requireContext(), "firstttt", Toast.LENGTH_SHORT).show()
        Toast.makeText(requireContext(), "compID: " + userId, Toast.LENGTH_SHORT).show()

        myRef.orderByChild("companyID").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Toast.makeText(requireContext(), "secondsss", Toast.LENGTH_SHORT).show()

                    publishedJobList.clear()
                    if (snapshot.exists()) {
                        for (publishedSnap in snapshot.children) {
                            val publishedList = publishedSnap.getValue(Job::class.java)
                            publishedList?.let {
                                Toast.makeText(requireContext(), "thridss", Toast.LENGTH_SHORT).show()

                                publishedJobList.add(it)
                            }
                        }

                        // Log the size of the list
                        Log.d("PublishedJobListSize", publishedJobList.size.toString())

                        // Check if the list is empty or not
                        if (publishedJobList.isEmpty()) {
                            Log.d("PublishedJobList", "The list is empty")
                        } else {
                            Log.d("PublishedJobList", "The list is not empty")
                        }

                        val adapter = PublishedJobListAdapter()
                        adapter.setData(publishedJobList)
                        recyclerView.adapter = adapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
                }
            })
    }
     */


/*
    private fun fetchPublishedJobData() {
        // Fetch data from Firebase
        Toast.makeText(requireContext(), "fristtttt", Toast.LENGTH_SHORT).show()
        Toast.makeText(requireContext(), "fetch match ID : " + userId, Toast.LENGTH_SHORT).show()

        myRef.orderByChild("companyID").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("fetchPublishedJobData", "onDataChange called")

                // Process data and create adapter
                Toast.makeText(requireContext(), "secondssss", Toast.LENGTH_SHORT).show()

                val publishedJobList = mutableListOf<Job>()
                for (publishedSnap in snapshot.children) {
                    val publishedList = publishedSnap.getValue(Job::class.java)
                    if (publishedList != null && publishedList.companyID.toString() == userId) {
                        Toast.makeText(requireContext(), "thirddd", Toast.LENGTH_SHORT).show()

                        publishedJobList.add(publishedList)
                    }
                }
                val adapter = PublishedJobListAdapter(publishedJobList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }*/


    /* private fun fetchPublishedJobData() {
         Toast.makeText(context, "fristtttt", Toast.LENGTH_SHORT).show()

         myRef.addValueEventListener(object:ValueEventListener{
             override fun onDataChange(snapshot:DataSnapshot){
                 Toast.makeText(requireContext(), "secondssss", Toast.LENGTH_SHORT).show()
                 publishedJobList.clear()

                 if(snapshot.exists()){
                     for(publishedSnap in snapshot.children){
                         val publishedList = publishedSnap.getValue(Job::class.java)
                         if (publishedList != null) {
                                 if(publishedList.companyID.toString() == userId) {
                                 Toast.makeText(requireContext(), "thirddd", Toast.LENGTH_SHORT).show()
                                 publishedJobList.add(publishedList!!)
                             }
                         }
                     }
                     val adapter = PublishedJobListAdapter()
                     adapter.setData(publishedJobList)
                     recyclerView.adapter = adapter
                 }
             }
             override fun onCancelled(error:DatabaseError){
                 Toast.makeText(requireContext(),"Error: $error",Toast.LENGTH_LONG).show()
             }
         })
     }*/
        //fetch data from firebase
       /* myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(requireContext(), "Received job data", Toast.LENGTH_SHORT).show()

                //handle data retrieval
                val newPublishedJobList = mutableListOf<Job>()
                for (jobSnapshot in snapshot.children) {
                    val job = jobSnapshot.getValue(Job::class.java)
                    job?.let {
                        if (job.companyID == userId) {
                            newPublishedJobList.add(it)
                        }
                    }
                }

                // Update the list and notify the adapter
                publishedJobList.clear()
                publishedJobList.addAll(newPublishedJobList)
                recyclerView.adapter?.notifyDataSetChanged()

                // Optional: Display a toast message for debugging
                Toast.makeText(requireContext(), "Received ${publishedJobList.size} job(s)", Toast.LENGTH_SHORT).show()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })*/


        /*myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(context, "fewofieoif", Toast.LENGTH_SHORT).show()
                publishedJobList.clear()
                if(snapshot.exists()) {
                    Toast.makeText(context, "???????????", Toast.LENGTH_SHORT).show()
                    for (jobSnap in snapshot.children) {
                        val job = jobSnap.getValue(Job::class.java)
                        Toast.makeText(requireContext(), "secondsss", Toast.LENGTH_SHORT).show()
                        if (job != null) {
                            if(job.companyID == userId) {
                                publishedJobList.add(job)
                            }
                        }
                    }

                    val adapter = PublishedJobListAdapter()
                    adapter.setData(publishedJobList)
                    recyclerView.adapter = adapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })*/



}
