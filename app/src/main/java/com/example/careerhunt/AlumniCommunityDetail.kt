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
import com.example.careerhunt.data.Alumni_community_comment
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
    private var dbRefAlumni : DatabaseReference        = db.database.getReference("Alumni")
    private var dbRefAlumniComment : DatabaseReference = db.database.getReference("Alumni_Comment ")
    private var dbRefPersonal : DatabaseReference      = db.database.getReference("Personal ")
    private lateinit var alumniCommentList : ArrayList<Alumni_community_comment>
    private lateinit var recyclerView : RecyclerView

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
        var personalId : String = ""
        Log.d("Post id is : ", postId.toString())
        //sent comment button
        val btnCommentSent : ImageButton = view.findViewById(R.id.btnSentComment)

        Log.d("halloebwekjfkwe", "dwrqwrqw")
        //Post Upper detail load
        //id successfully past inside here
        findAlumniById(postId.toString()){alumni ->
            tvTitle.text   = alumni?.title
            tvContent.text = alumni?.content
            tvDate.text    = calDay(alumni?.date.toString())
            //Toast.makeText(context, alumni?.personal_id.toString(), Toast.LENGTH_LONG).show()

            //something wrong here, this function  is not executing
            //priority: low, check by later
            findPersonalById(alumni?.personal_id.toString()){personalTemp ->
                Log.d("Something wrong here: " , "jlafhkakfha")

                tvUsername.text = personalTemp?.name
                tvSchool.text = personalTemp?.graduatedFrom
            }
        }

        recyclerView = view.findViewById(R.id.alumni_comment_recycle_view)
        alumniCommentList = arrayListOf<Alumni_community_comment>()
        fetchCommentDataByPostID(postId.toString())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

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
        btnCommentSent.setOnClickListener(){
            var etComment : EditText = view.findViewById(R.id.etComment)

            Log.d("button click", "here")
            //val postId = arguments?.getString("postId")

            //push data into firebase
            val senderId : String = "1"
            val alumni_comment = Alumni_community_comment(etComment.text.toString(), postId.toString(), senderId)
            dbRefAlumniComment.push().setValue(alumni_comment)

            //make it empty after sent
            etComment.setText("")
            Toast.makeText(requireContext(), "Comment Sent", Toast.LENGTH_SHORT).show()
        }


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
                    Log.d("Alumni = ", snapshot.getValue(Alumni::class.java).toString())
                    val alumni : Alumni? = snapshot.getValue(Alumni::class.java)
                    callback(alumni)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                callback(null)
            }
        })
    }

    private fun findPersonalById(id : String, callback: (PersonalTemp?) -> Unit){
        dbRefPersonal.child(id).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val personal : PersonalTemp? = snapshot.getValue(PersonalTemp::class.java)
                    Log.d("Personal findPersonal By id :  = ", personal?.name.toString())
                    Log.d("findPersonalId is run" , "running")
                    callback(personal)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                callback(null)
            }
        })
    }

    private fun fetchCommentDataByPostID(postId : String){
        dbRefAlumniComment.orderByChild("postId").equalTo(postId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                alumniCommentList.clear()
                if(snapshot.exists()) {
                    for (alumniCommentSnap in snapshot.children) {
                        val alumni_comment = alumniCommentSnap.getValue(Alumni_community_comment::class.java)
                        //so far no id in data class
                        //alumni_comment?.id = alumniCommentSnap.key.toString()
                        alumniCommentList.add(alumni_comment!!)
                    }
                    val adapter = AlumniCommunityComment_adapter()
                    adapter.setData(alumniCommentList)
                    recyclerView.adapter = adapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

}