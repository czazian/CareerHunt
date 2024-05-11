package com.example.careerhunt.dataAdapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.careerhunt.R
import com.example.careerhunt.data.Personal



class ViewApplicantAdapter ( ) : RecyclerView.Adapter <ViewApplicantAdapter.ViewApplicantHolder>(){

    private var personalList = emptyList<Personal>()
    //private lateinit var storageRef : StorageReference


    class ViewApplicantHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val tvApplicName : TextView = itemView.findViewById(R.id.tvApplicName)
        val tvApplicEmail : TextView = itemView.findViewById(R.id.tvApplicEmail)
        //val img : ImageView = itemView.findViewById(R.id.imgPhoto)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewApplicantHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_applicant_holder, parent, false )
        return ViewApplicantHolder(itemView)
    }

    override fun getItemCount(): Int {
        return personalList.size
    }

    override fun onBindViewHolder(holder: ViewApplicantHolder, position: Int) {
        val currentItem = personalList[position]

        // val ref = storageRef.child("imgProfile/").child(currentItem.id + ".pgn") // storageRef --> refering to the root directory
        holder.tvApplicName.text = currentItem.name
        holder.tvApplicEmail.text = currentItem.email

        /*ref.downloadUrl
            .addOnCompleteListener{
                Glide.with(holder.img).load(it.result.toString()).into(holder.img)// load it into
            }
        holder.img*/
    }

    fun setData(personalList: List<Personal>){
        this.personalList = personalList

        notifyDataSetChanged()
    }



}