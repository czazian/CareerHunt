package com.example.careerhunt.dataAdapter

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.AlumniCommunityDetail
import com.example.careerhunt.R
import com.example.careerhunt.data.Alumni
import com.example.careerhunt.data.Personal
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class AlumniCommunityYourPost_adapter(private val context: android.content.Context) : RecyclerView.Adapter <AlumniCommunityYourPost_adapter.MyViewHolder>() {

    private var alumniCommunityList = emptyList<Alumni>()
    private var dbRefPersonal : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Personal")
    private var dbRefAlumni : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Alumni")
    private var dbRefAlumniComment : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Alumni_Comment")
    private val sharedIDPreferences = context.getSharedPreferences("userid", android.content.Context.MODE_PRIVATE)
    private val currentLoginPersonalId : String = sharedIDPreferences.getString("userid", "") ?: ""

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val tvPostId : TextView = itemView.findViewById(R.id.tvPostId)
        val imgBtnDel : ImageButton = itemView.findViewById(R.id.imgBtnDelete)
        val tvTime : TextView = itemView.findViewById(R.id.tvTime)
        val tvTitle : TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent : TextView = itemView.findViewById(R.id.tvContent)
        val tvLike : TextView = itemView.findViewById(R.id.tvLikeNum)
        val tvView : TextView = itemView.findViewById(R.id.tvViewNum)
        val tvCR : TextView = itemView.findViewById(R.id.tvViewCRNum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.alumni_list_item_your_post, parent, false )
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AlumniCommunityYourPost_adapter.MyViewHolder, position: Int) {

        val currentItem = alumniCommunityList[position]

        holder.tvPostId.text = currentItem.id.toString()
        holder.tvTime.text = currentItem.date + " (" + calDay(currentItem.date) + ")"
        holder.tvTitle.text = currentItem.title.toString()
        holder.tvContent.text = currentItem.content.toString()
        holder.tvLike.text = currentItem.personal_liked.size.toString()
        holder.tvView.text = currentItem.personal_view.size.toString()
        holder.tvCR.text = calCR(currentItem.personal_liked.size, currentItem.personal_view.size).toString() + "%"

        holder.imgBtnDel.setOnClickListener(){

            MaterialAlertDialogBuilder(context)
                .setTitle("Are you sure to delete this post?")
                .setMessage("Deleted post never to be recovered")
                .setPositiveButton("Delete") { dialog, _ ->
                    // Do something when positive button is clicked
                    //delete the clicked post
                    dbRefAlumni.child(currentItem.id).removeValue()

                    //delete the comment
                    dbRefAlumniComment.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                val postComment = snapshot.child("postId").getValue(String::class.java)
                                if (postComment == currentItem.id) {
                                    snapshot.ref.removeValue().addOnSuccessListener {
                                        Log.d("Firebase", "Record removed successfully")
                                    }.addOnFailureListener { exception ->
                                        Log.e("Firebase", "Failed to remove record: ${exception.message}")
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Do something when negative button is clicked
                    dialog.dismiss()
                }
                .show()

        }

        //when a list is clicked, go to its detail page
        holder.itemView.findViewById<View>(R.id.constraintLayout1).setOnClickListener {

            val bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            findPersonalById(currentItem.personal_id){personal ->
                bundle.putString("userId", personal!!.personalID.toString())
                bundle.putString("username", personal!!.name)
                bundle.putString("school", personal.graduatedFrom)
            }

            // Handle post item click
            val context = holder.itemView.context

            val fragment = AlumniCommunityDetail()
            fragment.arguments = bundle

            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return alumniCommunityList.size
    }


    fun setData(alumniCommunityList: ArrayList<Alumni>){
        this.alumniCommunityList = alumniCommunityList
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calDay(date : String) : String {
        // Given date
        val givenDate: LocalDate = LocalDate.parse(date)
        // Today's date
        val today: LocalDate = LocalDate.now()
        // Calculate the difference in days
        val daysDifference: Long = ChronoUnit.DAYS.between(today, givenDate).absoluteValue

        val result : String

        if(daysDifference > 0){ // few days ago
            result = daysDifference.toString() + " days ago"
        }
        else if(daysDifference == 0.toLong()){ //today
            result = "Today"
        }
        else{
            result = "null"
        }
        return result
    }


    private fun findPersonalById(id : String, callback: (Personal?) -> Unit){
        dbRefPersonal.child(id).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    Log.d("Snapshot = ", snapshot.getValue(Personal::class.java).toString())
                    val personal : Personal? = snapshot.getValue(Personal::class.java)
                    Log.d("Personal :  = ", personal?.name.toString())
                    callback(personal)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(Context, "Error: $error", Toast.LENGTH_LONG).show()
                callback(null)
            }
        })
    }

    private fun calCR(like : Int, view : Int) : Double{
        return like.toDouble()/view.toDouble() * 100
    }
}