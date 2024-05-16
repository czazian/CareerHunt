package com.example.careerhunt

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.careerhunt.data.Alumni
import com.example.careerhunt.data.Alumni_community_comment
import com.example.careerhunt.dataAdapter.AlumniCommunityComment_adapter
import com.example.careerhunt.dataAdapter.AlumniCommunityYourPost_adapter
import com.example.careerhunt.dataAdapter.AlumniCommunity_adapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class AlumniCommunityDetail : Fragment() {

    private var db : DatabaseReference = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private var dbRefAlumni : DatabaseReference        = db.database.getReference("Alumni")
    private var dbRefAlumniComment : DatabaseReference = db.database.getReference("Alumni_Comment ")
    private lateinit var alumniCommentList : ArrayList<Alumni_community_comment>
    private lateinit var recyclerView : RecyclerView
    private lateinit var storageRef: StorageReference
    private lateinit var tvComment : TextView

    private lateinit var sharedIDPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        val currentLoginPersonalId : String = sharedIDPreferences.getString("userid", "") ?: ""

        val view = inflater.inflate(R.layout.fragment_alumni_community_detail, container, false)
        val tvUsername : TextView = view.findViewById(R.id.tvUsername)
        val tvSchool : TextView = view.findViewById(R.id.tvSchool)
        val imgProfile : ImageView = view.findViewById(R.id.imgViewProfilePostDetail)
        val tvTitle : TextView = view.findViewById(R.id.tvTitle)
        val tvContent : TextView = view.findViewById(R.id.tvContent)
        val tvDate : TextView = view.findViewById(R.id.tvTime)
        val tvLikeNum : TextView = view.findViewById(R.id.tvLikeNum)
        val btnLike : ImageButton = view.findViewById(R.id.btnLike)
        val btnShare : ImageButton = view.findViewById(R.id.btnShare)

        tvComment = view.findViewById(R.id.tvComment)

        val postId : String? = arguments?.getString("postId")

        //sent comment button
        val btnCommentSent : ImageButton = view.findViewById(R.id.btnSentComment)
        val btnBack : ImageButton = view.findViewById(R.id.imgBtnBack)
        //Post Upper detail load
        //id successfully past inside here
        dbRefAlumni.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                findAlumniById(postId.toString()){alumni ->

                    tvUsername.text = arguments?.getString("username")
                    tvSchool.text = arguments?.getString("school")
                    tvTitle.text   = alumni?.title
                    tvContent.text = alumni?.content
                    tvDate.text    = calDay(alumni?.date.toString())
                    tvLikeNum.text = alumni?.personal_liked?.size.toString()

                    storageRef = FirebaseStorage.getInstance().getReference()
                    val ref = storageRef.child("imgProfile").child(arguments?.getString("userId") + ".png")

                    //do not downloadUrl img that does not exists
                    ref.metadata.addOnSuccessListener { metadata ->
                        ref.downloadUrl
                            .addOnCompleteListener {
                                Glide.with(imgProfile).load(it.result.toString()).into(imgProfile)
                            }.addOnFailureListener{
                                Log.d("Image profile fail download", "ERROR")
                            }
                    }.addOnFailureListener { exception ->
                        // File does not exist or some other error occurred
                        Log.e("Image Profile does not exists", "File does not exist: ${exception.message}")
                    }

                    if(alumni?.personal_liked?.contains(currentLoginPersonalId) == true){
                        val color = Color.parseColor("#FF0000")
                        btnLike.setColorFilter(color)
                    }

                    //when like button is click
                    btnLike.setOnClickListener(){
                        //determine it is like or unlike
                        if(alumni?.personal_liked?.contains(currentLoginPersonalId) == true){
                            //unlike
                            alumni.personal_liked.remove(currentLoginPersonalId)
                            val color = Color.parseColor("#000000")
                            btnLike.setColorFilter(color)
                        }
                        else{
                            //like
                            val color = Color.parseColor("#FF0000")
                            btnLike.setColorFilter(color)
                            alumni!!.personal_liked.add(currentLoginPersonalId)
                            Toast.makeText(requireContext(), "Thank you for your like", Toast.LENGTH_SHORT).show()
                        }
                        dbRefAlumni.child(postId.toString()).child("personal_liked").setValue(alumni?.personal_liked)

                    }

                    //when share button is click
                    btnShare.setOnClickListener {
                        var data =  """
                            ALumni Post by ${arguments?.getString("username")},
                            
                            Title: ${alumni?.title}
                            Content: ${alumni?.content}
            
                            From CarrerHunt
                        """
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT, data)

                        // Start the activity with the sharing Intent
                        requireContext().startActivity(Intent.createChooser(intent, "Share to:"))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("cancel latest reflect : ", "cancelled")
            }
        })

        recyclerView = view.findViewById(R.id.alumni_comment_recycle_view)
        alumniCommentList = arrayListOf<Alumni_community_comment>()
        fetchCommentDataByPostID(postId.toString())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        //wait for whole database added first
        btnCommentSent.setOnClickListener(){
            var etComment : EditText = view.findViewById(R.id.etComment)

            Log.d("button click", "here")
            //val postId = arguments?.getString("postId")

            //push data into firebase
            val alumni_comment = Alumni_community_comment(etComment.text.toString(), postId.toString(), currentLoginPersonalId)
            dbRefAlumniComment.push().setValue(alumni_comment)

            //make it empty after sent
            etComment.setText("")
            Toast.makeText(requireContext(), "Comment Sent", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener(){
            val fragment = com.example.careerhunt.Alumni()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
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
                    checkEmptyCommentList(recyclerView.adapter as AlumniCommunityComment_adapter)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun checkEmptyCommentList(adapter : AlumniCommunityComment_adapter) {
        Log.d("item list in adapter = ", adapter.itemCount.toString())
        if (adapter.itemCount > 0) {
            tvComment.text = adapter.itemCount.toString() + " Comment(s)"
        } else {
            tvComment.text = "No Comment"
        }
    }

}