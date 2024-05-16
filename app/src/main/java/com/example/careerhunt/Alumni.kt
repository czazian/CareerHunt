package com.example.careerhunt

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Alumni
import com.example.careerhunt.dataAdapter.AlumniCommunity_adapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate

class Alumni : Fragment(), AlumniCommunity_adapter.onLikeButtonClick {

    private lateinit var dbRefAlumni : DatabaseReference
    private lateinit var dbRefPersonal : DatabaseReference
    private lateinit var alumniList : ArrayList<Alumni>
    private lateinit var recyclerView : RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //display fragment alumni
        val view = inflater.inflate(R.layout.fragment_alumni, container, false)

        // Assuming you are using Firebase Realtime Database
        dbRefAlumni = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Alumni")
        dbRefPersonal = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Personal")
        recyclerView = view.findViewById(R.id.alumni_recycle_view)
        alumniList = arrayListOf<Alumni>()
        fetchData()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        //when "+" button is click
        val btnAddAlumniComm : FloatingActionButton = view.findViewById(R.id.btnAddAlumniComm)
        btnAddAlumniComm.setOnClickListener(){
            val fragment = AlumniCommunityAdd()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        val imgBtnYourPost : ImageButton = view.findViewById(R.id.imgBtnMyPost)
        imgBtnYourPost.setOnClickListener(){
            val fragment = AlumniCommunityYourPost()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

    //when like button is clicked, run this
    override fun onLikeButtonClick(post: Alumni) {
        Toast.makeText(requireContext(), post.title.toString(), Toast.LENGTH_SHORT).show()
        //create record
        //alumniCommunityViewModal = ViewModelProvider(this).get(AlumniCommunityViewModel::class.java)
        //temp solution: should be personal ID of logined account of device
        //val alumniCommLike = Alumni_community_like(0, 1,  post.id)
        //alumniCommunityViewModal.addAlumniCommunityLike(alumniCommLike)
    }

    private fun fetchData(){
        dbRefAlumni.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                alumniList.clear()
                if(snapshot.exists()) {
                    for (alumniSnap in snapshot.children) {
                        val alumni = alumniSnap.getValue(Alumni::class.java)
                        alumni?.id = alumniSnap.key.toString()
                        alumniList.add(alumni!!)
                    }
                    val adapter = AlumniCommunity_adapter(requireContext())
                    adapter.setData(alumniList)
                    recyclerView.adapter = adapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

}