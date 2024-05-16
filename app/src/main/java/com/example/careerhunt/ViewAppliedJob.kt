package com.example.careerhunt

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Apply_Job
import com.example.careerhunt.data.Job
import com.example.careerhunt.data.Personal
import com.example.careerhunt.dataAdapter.AppliedJobListAdapter
import com.example.careerhunt.databinding.FragmentViewAppliedJobBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ViewAppliedJob : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedIDPreferences: SharedPreferences
    private lateinit var appliedJobList: ArrayList<Apply_Job>
    private lateinit var myRef: DatabaseReference

    private var userId: String = ""
    private lateinit var tvTotalCount: TextView
    private var totalAppliedJobs: Int = 0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_applied_job, container, false)

        // Access SharedPreferences from MainActivity
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        // Retrieve userId from SharedPreferences
        userId = sharedIDPreferences.getString("userid","") ?: ""

        // Initialize RecycleView
        recyclerView = view.findViewById(R.id.rvAppliedJob)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager here
        recyclerView.setHasFixedSize(true)


        // Fetch data from Firebase
        fetchAppliedJobData()

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
        }

        tvTotalCount = view.findViewById(R.id.tvTotalCount)


        return view
    }

    private fun fetchAppliedJobData() {
        // Firebase connection
        myRef = FirebaseDatabase.getInstance().getReference("Apply_Job")
        appliedJobList = arrayListOf()


        val query = myRef.orderByChild("personalID").equalTo(userId)// in data class is string type


        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TAG", "onDataChange")

                appliedJobList.clear()
                Log.d("TAG", snapshot.children.toString())
                if(snapshot.exists()) {
                    Log.d("TAG", "SNAPSHOT EXIST")
                    for (appliedSnap in snapshot.children) {

                        Log.d(ContentValues.TAG, "RetrieveValue")

                        val appliedJob = appliedSnap.getValue(Apply_Job::class.java)
                        appliedJobList.add(appliedJob!!)
                    }
                    val adapter = AppliedJobListAdapter()
                    adapter.setData(appliedJobList)
                    recyclerView.adapter = adapter
                    // get the total number of data (applied job num)
                    totalAppliedJobs = appliedJobList.size
                    tvTotalCount.text = totalAppliedJobs.toString()
                    adapter.notifyDataSetChanged()

                }else{
                    showErrorDialog()
                    tvTotalCount.text = "0"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showErrorDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
            .setMessage("No Record(s) Found")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }
}


