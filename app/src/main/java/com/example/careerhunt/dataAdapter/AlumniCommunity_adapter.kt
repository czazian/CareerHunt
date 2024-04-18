package com.example.careerhunt.dataAdapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.R
import com.example.careerhunt.data.AlumniCommunity
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date

class AlumniCommunity_adapter(private val alumniCommunityList: List<AlumniCommunity>) : RecyclerView.Adapter <AlumniCommunity_adapter.MyViewHolder>(){

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

        val tvUsername : TextView = itemView.findViewById(R.id.tvUsername)
        val tcSchool : TextView = itemView.findViewById(R.id.tvSchool)
        val tvTitle : TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent : TextView = itemView.findViewById(R.id.tvContent)
        val tvTime : TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.alumni_list_item, parent, false )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return alumniCommunityList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = alumniCommunityList[position]

        holder.tvUsername.text = currentItem.name
        holder.tcSchool.text = currentItem.school
        holder.tvTitle.text = currentItem.title
        holder.tvContent.text = currentItem.content
        holder.tvTime.text = currentItem.date.format(DateTimeFormatter.ISO_DATE)

    }
}