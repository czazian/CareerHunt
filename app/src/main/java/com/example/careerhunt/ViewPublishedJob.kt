package com.example.careerhunt

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Job
import com.example.careerhunt.dataAdapter.PublishedJobListAdapter
import com.example.careerhunt.databinding.FragmentViewPublishedJobBinding
import com.example.careerhunt.interfaces.JobInterface
import com.example.careerhunt.interfaces.UserInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewPublishedJob : Fragment() , UserInterface.RecyclerViewEvent{
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

        // Initialize RecycleView
        recyclerView = view.findViewById(R.id.rvPublishedJob)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager here
        recyclerView.setHasFixedSize(true)

        // Fetch data from Firebase
        fetchData()

       view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener() {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun fetchData(){
        val listerner = this

        // Firebase connection
        myRef = FirebaseDatabase.getInstance().getReference("Job")
        publishedJobList = arrayListOf()

        val query = myRef.orderByChild("companyID").equalTo(userId.toDouble())

        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TAG", "onDataChange")

                publishedJobList.clear()
                Log.d("TAG", snapshot.children.toString()) // not yet solve this issues, can display the result

                if(snapshot.exists()) {
                    Log.d("TAG", "SNAPSHOT EXIST")
                    for (publishedSnap in snapshot.children) {
                        Log.d(TAG, "RetrieveValue")

                        val publishedJob = publishedSnap.getValue(Job::class.java)
                        publishedJobList.add(publishedJob!!)
                    }
                    val adapter = PublishedJobListAdapter(listerner)
                    adapter.setData(publishedJobList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()

                }else{
                    showErrorDialog()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

// click on each RecycleView Item
    override fun onItemClick(position: Int) {
        //Get the clicked object
        val jobObj: Job = publishedJobList[position]
        val fragment = publishedJob_details()

        //Add Result Object into Bundle
        val bundle = Bundle()
        bundle.putSerializable("job", jobObj)
        fragment.arguments = bundle

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayout, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
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
