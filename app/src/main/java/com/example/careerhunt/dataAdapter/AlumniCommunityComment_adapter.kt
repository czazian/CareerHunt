package com.example.careerhunt.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.R
import com.example.careerhunt.data.Alumni_community
import com.example.careerhunt.data.Alumni_community_comment

class AlumniCommunityComment_adapter() : RecyclerView.Adapter <AlumniCommunityComment_adapter.MyViewHolder>() {

    private var alumniCommunityCommentList = emptyList<Alumni_community_comment>()

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

        val tvUsername : TextView = itemView.findViewById(R.id.tvUsername)
        val tvSchool : TextView = itemView.findViewById(R.id.tvSchool)
        val tvComment : TextView = itemView.findViewById(R.id.tvComment)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.alumni_comment_list, parent, false )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return alumniCommunityCommentList.size
    }

    override fun onBindViewHolder(holder: AlumniCommunityComment_adapter.MyViewHolder, position: Int) {
        val currentItem = alumniCommunityCommentList[position]

        holder.tvUsername.text = currentItem.personalID.toString()
        holder.tvSchool.text = currentItem.personalID.toString()
        holder.tvComment.text = currentItem.comment

    }

    fun setData(alumniCommunityCommentList: List<Alumni_community_comment>){
        this.alumniCommunityCommentList = alumniCommunityCommentList
        notifyDataSetChanged()
    }
}