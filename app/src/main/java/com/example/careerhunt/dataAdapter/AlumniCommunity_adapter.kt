package com.example.careerhunt.dataAdapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.AlumniCommunityDetail
import com.example.careerhunt.R
import com.example.careerhunt.data.Alumni
import com.example.careerhunt.data.PersonalTemp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class AlumniCommunity_adapter() : RecyclerView.Adapter <AlumniCommunity_adapter.MyViewHolder>(){

    interface onLikeButtonClick {
        fun onLikeButtonClick(post: Alumni)
    }

    private var alumniCommunityList = emptyList<Alumni>()
    private var dbRefPersonal : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Personal")
    private var dbRefAlumni : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Alumni")

    //modify this
    private val currentLoginPersonalId = "1"

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val tvUsername : TextView = itemView.findViewById(R.id.tvUsername)
        val tcSchool : TextView = itemView.findViewById(R.id.tvSchool)
        val tvTitle : TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent : TextView = itemView.findViewById(R.id.tvContent)
        val tvTime : TextView = itemView.findViewById(R.id.tvTime)
        val btnLike : ImageButton = itemView.findViewById(R.id.btnLike)
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

        var username : String = ""
        var school : String = ""
        findPersonalById(currentItem.personal_id){personal ->
            holder.tvUsername.text = personal?.name
            holder.tcSchool.text = personal?.graduatedFrom
            username = personal?.name.toString()
            school = personal?.graduatedFrom.toString()
        }
        //holder.tvUsername.text = currentItem.personal_id.toString()
        //holder.tcSchool.text = currentItem.personal_id.toString()
        holder.tvTitle.text = currentItem.title.toString()
        holder.tvContent.text = currentItem.content.toString()
        holder.tvTime.text = calDay(currentItem.date).toString()
        if(currentItem.personal_liked.contains(currentLoginPersonalId)){
            val color = Color.parseColor("#FF0000")
            holder.btnLike.setColorFilter(color)
        }

        //when a list is clicked, go to its detail page
        holder.itemView.findViewById<View>(R.id.constraintLayout1).setOnClickListener {

            val bundle = Bundle()
            bundle.putString("postId", currentItem.id)
            bundle.putString("username", username)
            bundle.putString("school", school)
            Log.d("key is : ", currentItem.id)

            //add personal if first time view the detail of a post
            if(!currentItem.personal_view.contains(currentLoginPersonalId)){
                //add
                currentItem.personal_view.add(currentLoginPersonalId)
                dbRefAlumni.child(currentItem.id).child("personal_view").setValue(currentItem.personal_view)
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

        //when like button is click
        holder.itemView.findViewById<View>(R.id.btnLike).setOnClickListener {

            Log.d("Like is click : ", "postID : " + currentItem.id)
            Log.d("Like by personal : ",  currentItem.personal_liked.toString())
            //determine it is like or unlike
            if(currentItem.personal_liked.contains(currentLoginPersonalId)){
                //unlike
                currentItem.personal_liked.remove(currentLoginPersonalId)
                val color = Color.parseColor("#000000")
                holder.btnLike.setColorFilter(color)
            }
            else{
                //like
                currentItem.personal_liked.add(currentLoginPersonalId)
                val color = Color.parseColor("#FF0000")
                holder.btnLike.setColorFilter(color)
            }
            dbRefAlumni.child(currentItem.id).child("personal_liked").setValue(currentItem.personal_liked)

        }

        //when comment button is click
        holder.itemView.findViewById<View>(R.id.btnComment).setOnClickListener {
            val bundle = Bundle()
            bundle.putString("postId", currentItem.title.toString())

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

        //when share button is click
        holder.itemView.findViewById<View>(R.id.btnShare).setOnClickListener {
            var data = "haha"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, data)

            val context = holder.itemView.context
            // Start the activity with the sharing Intent
            context.startActivity(Intent.createChooser(intent, "Share to:"))
        }

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

    private fun findPersonalById(id : String, callback: (PersonalTemp?) -> Unit){
        dbRefPersonal.child(id).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    Log.d("Snapshot = ", snapshot.getValue(PersonalTemp::class.java).toString())
                    val personal : PersonalTemp? = snapshot.getValue(PersonalTemp::class.java)
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

}

