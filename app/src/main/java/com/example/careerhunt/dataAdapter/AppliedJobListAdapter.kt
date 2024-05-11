package com.example.careerhunt.dataAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.R
import com.example.careerhunt.data.Apply_Job
import com.example.careerhunt.data.Job
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AppliedJobListAdapter() : RecyclerView.Adapter <AppliedJobListAdapter.MyViewHolder>(){
    private var applyJobList = emptyList<Apply_Job>()
    private var dbRefPersonal : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Personal")
    private var dbRefJob : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Job")

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvJobCategory: TextView = itemView.findViewById(R.id.tvJobCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_applied_holder, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return applyJobList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = applyJobList[position]
        // Fetch job data based on jobID from the Apply_Job
        fetchJobData(currentItem.jobID) { job ->
            holder.tvJobTitle.text = job.jobName
            holder.tvJobCategory.text = job.jobCategory
        }
    }

    private fun fetchJobData(jobId: Long?, callback: (Job) -> Unit) {
        val jobRef = FirebaseDatabase.getInstance().getReference("Job").child("1")
        jobRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val job = dataSnapshot.getValue(Job::class.java)
                job?.let { callback(it) }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error fetching job data: ${databaseError.message}")
            }
        })
    }

    fun setData(applyJobList: List<Apply_Job>) {
        this.applyJobList = applyJobList
        notifyDataSetChanged()
    }
}



