package com.example.careerhunt

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Alumni
import com.example.careerhunt.data.AlumniCommunityViewModel
import com.example.careerhunt.data.Alumni_community
import com.example.careerhunt.data.Alumni_community_like
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

    private lateinit var alumniCommunityViewModal : AlumniCommunityViewModel
    val database = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Alumni")


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //display fragment alumni
        val view = inflater.inflate(R.layout.fragment_alumni, container, false)

        alumniCommunityViewModal = ViewModelProvider(this).get(AlumniCommunityViewModel::class.java)

        //should be logined user id
        //val alumni_like_list_personal : LiveData<List<Alumni_community_like>> = alumniCommunityViewModal.findLikeByPersonalId(1)

        // Assuming you are using Firebase Realtime Database
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Alumni")

        var dataList = arrayListOf<Alumni>()
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.key ?: ""
                    val title = snapshot.child(id).child("title").getValue(String::class.java) ?: ""
                    val content = snapshot.child(id).child("content").getValue(String::class.java) ?: ""
                    val personal_id = snapshot.child(id).child("personal_id").getValue(String::class.java) ?: ""

                    val data = Alumni(title, content, "2034-5-8", personal_id)
                    dataList.add(data)
                }
            }
            
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }
        })

        Toast.makeText(requireContext(), dataList.size, Toast.LENGTH_SHORT).show()
        val adapter = AlumniCommunity_adapter(this)
        val recyclerView: RecyclerView = view.findViewById(R.id.alumni_recycle_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        alumniCommunityViewModal = ViewModelProvider(this).get(AlumniCommunityViewModel::class.java)
        alumniCommunityViewModal.readAllData().observe(viewLifecycleOwner, Observer {alumniCommunityList->
            adapter.setData(dataList)
        })


        //when "+" button is click
        val btnAddAlumniComm : FloatingActionButton = view.findViewById(R.id.btnAddAlumniComm)
        btnAddAlumniComm.setOnClickListener(){
            val fragment = AlumniCommunityAdd()
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
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                personList.clear()
                if(snapshot.exists()) {
                    for (personSnap in snapshot.children) {
                        val person = personSnap.getValue(Person::class.java)
                        personList.add(person!!)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

}