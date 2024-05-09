package com.example.careerhunt

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Alumni
import com.example.careerhunt.data.PersonalTemp
import com.example.careerhunt.dataAdapter.AlumniCommunityComment_adapter
import com.example.careerhunt.dataAdapter.AlumniCommunity_adapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class AlumniCommunityDetail : Fragment() {

    private var db : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private var dbRefAlumni : DatabaseReference = db.database.getReference("Alumni")
    private var dbRefPersonal : DatabaseReference = db.database.getReference("Personal ")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alumni_community_detail, container, false)
        val tvUsername : TextView = view.findViewById(R.id.tvUsername)
        val tvSchool : TextView = view.findViewById(R.id.tvSchool)
        val tvTitle : TextView = view.findViewById(R.id.tvTitle)
        val tvContent : TextView = view.findViewById(R.id.tvContent)
        val tvDate : TextView = view.findViewById(R.id.tvTime)

        val postId : String? = arguments?.getString("postId")
        Log.d("Post id is : ", postId.toString())
        //sent comment button
        //val btnCommentSent : ImageButton = view.findViewById(R.id.btnCommentSent)

        //Post Upper detail load
        //id successfully past inside here
        findAlumniById(postId.toString()){alumni ->
            tvTitle.text   = alumni?.title
            tvContent.text = alumni?.content
            tvDate.text    = calDay(alumni?.date.toString())

            findPersonalById(alumni?.personal_id.toString()){personal ->
                tvUsername.text = personal?.name
                tvSchool.text = personal?.graduatedFrom
            }
        }



        //Post bottom detail (comment) load
        //val adapter = AlumniCommunityComment_adapter()
        //val recyclerView: RecyclerView = view.findViewById(R.id.alumni_comment_recycle_view)
        //val tvComment : TextView =  view.findViewById(R.id.tvComment)
        //recyclerView.adapter = adapter
        //recyclerView.layoutManager = LinearLayoutManager(context)
        //recyclerView.setHasFixedSize(true)
        //alumniCommunityViewModel = ViewModelProvider(this).get(AlumniCommunityViewModel::class.java)
        //alumniCommunityViewModel.findCommentbyPostId(postId.toInt()).observe(viewLifecycleOwner, Observer {alumniCommunityCommentList->
         //   adapter.setData(alumniCommunityCommentList)

         //   if(adapter.itemCount >= 1){
        //        tvComment.text = adapter.itemCount.toString() + " Comment(s)"
       //     }else{
        //        tvComment.text = "No comment"
        //    }

       // })


        //wait for whole database added first
        //btnCommentSent.setOnClickListener(){
           // var etComment   : EditText = view.findViewById(R.id.editText)

            //val postId = arguments?.getString("postId")

            //val alumniCommComment = postId?.toInt()
            //    ?.let { it1 -> Alumni_community_comment(0, etComment.text.toString(), 1, it1) }
            //if (alumniCommComment != null) {
            //    alumniCommunityViewModel.addAlumniCommunityComment(alumniCommComment)
            //}

            //make it empty after sent
            //etComment.setText("")
            //Toast.makeText(requireContext(), "Post successful:" + etComment.text.toString(), Toast.LENGTH_LONG).show()
        //}


        // Inflate the layout for this fragment
        return view
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

    private fun findAlumniById(id : String, callback: (Alumni?) -> Unit){
        dbRefAlumni.child(id).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    Log.d("Snapshot = ", snapshot.getValue(Alumni::class.java).toString())
                    val alumni : Alumni? = snapshot.getValue(Alumni::class.java)
                    Log.d("Alumni :  = ", alumni?.title.toString())
                    callback(alumni)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(Context, "Error: $error", Toast.LENGTH_LONG).show()
                callback(null)
            }
        })
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