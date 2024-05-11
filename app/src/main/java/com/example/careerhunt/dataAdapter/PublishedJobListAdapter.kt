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

class PublishedJobListAdapter() : RecyclerView.Adapter<PublishedJobListAdapter.ViewPublishedHolder>() {


    private var publishedJobList = emptyList<Job>()

    class ViewPublishedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvJobCategory: TextView = itemView.findViewById(R.id.tvJobCategory)
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

        holder.tvJobTitle.text = currentItem.jobName
        holder.tvJobCategory.text = currentItem.jobCategory
    }


   fun setData(publishedJobList: List<Job>) {
        this.publishedJobList = publishedJobList
        notifyDataSetChanged()
    }

}