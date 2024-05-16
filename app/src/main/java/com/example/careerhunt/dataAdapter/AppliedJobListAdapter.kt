package com.example.careerhunt.dataAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.careerhunt.R
import com.example.careerhunt.data.Apply_Job
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Job
import com.example.careerhunt.data.Personal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AppliedJobListAdapter() : RecyclerView.Adapter<AppliedJobListAdapter.MyViewHolder>() {
    private var appliedJobList = emptyList<Apply_Job>()
    private lateinit var myRef: DatabaseReference
    private lateinit var storageRef : StorageReference

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvCompName: TextView = itemView.findViewById(R.id.tvCompName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val compLogo : ImageView = itemView.findViewById(R.id.compLogo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_applied_holder, parent, false)
        storageRef = FirebaseStorage.getInstance().getReference()

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return appliedJobList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = appliedJobList[position]
        // Fetch job data based on jobID from the Apply_Job

        fetchJobData(currentItem.jobID) { job,company ->
            if (job != null && appliedJobList.getOrNull(position)?.jobID == job.jobID) {
                holder.tvJobTitle.text = job.jobName
                holder.tvLocation.text = job.jobLocationCity
            }
            if(company!=null){
                holder.tvCompName.text = company.compName

               val ref = storageRef.child("compProfile").child(company.companyID.toString() + ".png")
                ref.downloadUrl
                    .addOnCompleteListener {
                        Glide.with(holder.compLogo).load(it.result.toString()).into(holder.compLogo)
                    }

            }
        }
    }

    // Retrieve Job data
    private fun fetchJobData(jobId: String, callback: (Job?, Company?) -> Unit) {
        myRef = FirebaseDatabase.getInstance().getReference("Job").child(jobId)


        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val job = dataSnapshot.getValue(Job::class.java)

                    // Get the companyID from the job
                    val companyID = job?.companyID ?: return

                    // Fetch company data based on companyID
                    fetchCompanyData(companyID) { company ->
                        callback(job, company)
                    }

                } else {
                    // no record match with the jobId
                    callback(null, null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error fetching job data: ${databaseError.message}")
                // Pass null to the callback to indicate an error
                callback(null,null)
            }
        })
    }


    // Retrieve Company Data
        private fun fetchCompanyData(companyID: Int, callback: (Company?) -> Unit) {
        val companyRef =
            FirebaseDatabase.getInstance().getReference("Company").child(companyID.toString())

        companyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val company = dataSnapshot.getValue(Company::class.java)
                    callback(company)
                } else {
                    // no record match with the companyID
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error fetching company data: ${databaseError.message}")
                // Pass null to the callback to indicate an error
                callback(null)
            }
        })
    }



        fun setData(applyJobList: List<Apply_Job>) {
            this.appliedJobList = applyJobList
            notifyDataSetChanged()
        }
    }




