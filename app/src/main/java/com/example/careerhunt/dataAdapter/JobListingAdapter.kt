package com.example.careerhunt.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Job
import com.example.careerhunt.R
import com.example.careerhunt.interfaces.JobInterface
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


//Need to add private val listener: JobViewModel.RecyclerViewEvent - Click on different items in the recyclerview
class JobListingAdapter (private var listener: JobInterface.RecyclerViewEvent):  RecyclerView.Adapter <JobListingAdapter.JobListingViewHolder>() {

    private var jobList = emptyList<Job>()
    private lateinit var storageRef : StorageReference

    inner class JobListingViewHolder (itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        //Company Details
        val lblCompanyName: TextView = itemView.findViewById(R.id.lblCompanyName)
        val companyImage: ImageView = itemView.findViewById(R.id.imgCompany)

        //Job Details
        val lblJobName: TextView = itemView.findViewById(R.id.lblJobTitle)
        val lblJobDescription: TextView = itemView.findViewById(R.id.lblDesc)
        val lblJobType: TextView = itemView.findViewById(R.id.lblType)
        val lblLocation: TextView = itemView.findViewById(R.id.lblLocation)


        //RecyclerViewInterface Members - Click on different items in the recyclerview
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobListingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job_viewholder, parent, false)

        storageRef = FirebaseStorage.getInstance().getReference()

        return JobListingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    //Bind Data from Database to View
    override fun onBindViewHolder(holder: JobListingViewHolder, position: Int) {
        //Get item clicked
        val currentItem = jobList[position]

        //Check length of description and limit it
        val words = currentItem.jobDescription.toString()
        val wordsTrimmed = currentItem.jobDescription.toString().trim()
        val numberOfWords = wordsTrimmed.split("\\s+".toRegex()).size

        val first50Characters = words.substring(0, minOf(words.length, 50))

        if(numberOfWords > 50){
            holder.lblJobDescription.text = "$first50Characters..."
        } else {
            holder.lblJobDescription.text = words
        }

        //Bind value
        //val ref = storageRef.child("imgProfile").child(currentItem.jobID.toString() + ".png")
        holder.lblJobName.text = currentItem.jobName
        holder.lblJobType.text = currentItem.jobType
        holder.lblLocation.text = currentItem.jobLocationState + ", " + currentItem.jobLocationCity

    }


    //Get all result from Fragment.kt, to allow get item one by one
    fun setData(jobList: List<Job>){
        this.jobList = jobList
        notifyDataSetChanged()
    }


}