package com.example.careerhunt.dataAdapter


import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.careerhunt.R
import com.example.careerhunt.data.Personal
import com.example.careerhunt.interfaces.JobInterface
import com.example.careerhunt.interfaces.UserInterface
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ViewApplicantAdapter(private var listener: UserInterface.RecyclerViewEvent) : RecyclerView.Adapter<ViewApplicantAdapter.ViewApplicantHolder>() {

    private var personalList = emptyList<Personal>()
    private lateinit var storageRef: StorageReference


    inner class ViewApplicantHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tvApplicName: TextView = itemView.findViewById(R.id.tvApplicName)
        val tvApplicEmail: TextView = itemView.findViewById(R.id.tvApplicEmail)
        val profilepic: ImageView = itemView.findViewById(R.id.profilePic)

        // Allow clicking on the RecycleView
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewApplicantHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_applicant_holder, parent, false)
        storageRef = FirebaseStorage.getInstance().getReference()

        return ViewApplicantHolder(itemView)
    }

    override fun getItemCount(): Int {
        return personalList.size
    }

    override fun onBindViewHolder(holder: ViewApplicantHolder, position: Int) {
        val currentItem = personalList[position]

        holder.tvApplicName.text = currentItem.name
        holder.tvApplicEmail.text = currentItem.email

        val ref = storageRef.child("imgProfile/").child(currentItem.personalID.toString() + ".png")
        ref.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                if (downloadUrl.isNotEmpty()) {
                    Glide.with(holder.profilepic).load(downloadUrl).into(holder.profilepic)
                }
            }
        }
    }

    fun setData(personalList: List<Personal>) {
        this.personalList = personalList

        notifyDataSetChanged()
    }

}