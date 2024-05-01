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
import com.example.careerhunt.viewModel.CompanyViewModel
import com.example.careerhunt.viewModel.JobViewModel


//Need to add private val listener: JobViewModel.RecyclerViewEvent - Click on different items in the recyclerview
class JobListingAdapter (private val listener: JobViewModel.RecyclerViewEvent, private val companyViewModel: CompanyViewModel, private val lifecycleOwner: LifecycleOwner):  RecyclerView.Adapter <JobListingAdapter.JobListingViewHolder>() {

    private var jobList = emptyList<Job>()

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
        return JobListingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    //Bind Data from Database to View
    override fun onBindViewHolder(holder: JobListingViewHolder, position: Int) {
        val currentItem = jobList[position]
        holder.lblJobName.text = currentItem.jobName
        holder.lblJobDescription.text = currentItem.jobDescription
        holder.lblJobType.text = currentItem.jobType
        holder.lblLocation.text = currentItem.jobLocation


        //Use the Foreign Key (from Job) to retrieve records (from Company)
        var companyID:Int = currentItem.companyID
        companyViewModel.readCompany(companyID.toString().toInt()).observe(lifecycleOwner, Observer { aCompany ->
            holder.lblCompanyName.text = aCompany.compName.toString()
        })
    }


    //Get all result from Fragment.kt, to allow get item one by one
    fun setData(jobList: List<Job>){
        this.jobList = jobList
        notifyDataSetChanged()
    }
}