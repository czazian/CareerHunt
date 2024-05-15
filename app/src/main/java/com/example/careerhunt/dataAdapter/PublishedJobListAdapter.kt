package com.example.careerhunt.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.R
import com.example.careerhunt.ViewPublishedJob
import com.example.careerhunt.data.Job
import com.example.careerhunt.interfaces.JobInterface
import com.example.careerhunt.interfaces.UserInterface

class PublishedJobListAdapter(private var listener: UserInterface.RecyclerViewEvent) : RecyclerView.Adapter<PublishedJobListAdapter.ViewPublishedHolder>() {


    private var publishedJobList = emptyList<Job>()

    inner class ViewPublishedHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvJobCategory: TextView = itemView.findViewById(R.id.tvJobCategory)
        val tvCount : TextView = itemView.findViewById(R.id.tvNum)


        // Allow clicking on the RecycleView
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



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPublishedHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_published_holder, parent, false)

        return ViewPublishedHolder(itemView)
    }

    override fun getItemCount(): Int {
        return publishedJobList.size
    }

    override fun onBindViewHolder(holder: ViewPublishedHolder, position: Int) {
        val currentItem = publishedJobList[position]
        var count = position + 1
        holder.tvJobTitle.text = currentItem.jobName
        holder.tvJobCategory.text = currentItem.jobCategory
        holder.tvCount.text = count.toString()
    }


   fun setData(publishedJobList: List<Job>) {
        this.publishedJobList = publishedJobList
        notifyDataSetChanged()
    }

}