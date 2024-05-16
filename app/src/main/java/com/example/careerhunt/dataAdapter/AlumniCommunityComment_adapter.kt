package com.example.careerhunt.dataAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.careerhunt.R
import com.example.careerhunt.data.Alumni_community_comment
import com.example.careerhunt.data.Personal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AlumniCommunityComment_adapter() : RecyclerView.Adapter <AlumniCommunityComment_adapter.MyViewHolder>() {

    private var alumniCommunityCommentList = emptyList<Alumni_community_comment>()
    private var dbRefPersonal : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Personal")
    private lateinit var storageRef: StorageReference

    class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

        val tvUsername : TextView = itemView.findViewById(R.id.tvUsername)
        val tvSchool : TextView = itemView.findViewById(R.id.tvSchool)
        val tvComment : TextView = itemView.findViewById(R.id.tvCommentShow )
        val imgProfileComment : ImageView = itemView.findViewById(R.id.imgViewProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.alumni_comment_list, parent, false )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return alumniCommunityCommentList.size
    }

    override fun onBindViewHolder(holder: AlumniCommunityComment_adapter.MyViewHolder, position: Int) {
        val currentItem = alumniCommunityCommentList[position]

        holder.tvComment.text = currentItem.comment
        findPersonalById(currentItem.personalId){personal ->
            holder.tvUsername.text = personal?.name
            holder.tvSchool.text = personal?.graduatedFrom

            //Get image user
            storageRef = FirebaseStorage.getInstance().getReference()
            val ref = storageRef.child("imgProfile").child(personal?.personalID.toString() + ".png")

            //do not downloadUrl img that does not exists
            ref.metadata.addOnSuccessListener { metadata ->
                ref.downloadUrl
                    .addOnCompleteListener {
                        Glide.with(holder.imgProfileComment).load(it.result.toString()).into(holder.imgProfileComment)
                    }.addOnFailureListener{
                        Log.d("Image profile fail download", "ERROR")
                    }
            }.addOnFailureListener { exception ->
                // File does not exist or some other error occurred
                Log.e("Image Profile does not exists", "File does not exist: ${exception.message}")
            }
        }

    }

    fun setData(alumniCommunityCommentList: List<Alumni_community_comment>){
        this.alumniCommunityCommentList = alumniCommunityCommentList
        notifyDataSetChanged()
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
}