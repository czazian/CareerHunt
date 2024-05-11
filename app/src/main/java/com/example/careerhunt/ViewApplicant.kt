package com.example.careerhunt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.Personal
import com.example.careerhunt.dataAdapter.ViewApplicantAdapter
import com.example.careerhunt.databinding.FragmentViewApplicantBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewApplicant : Fragment() {
    private lateinit var binding: FragmentViewApplicantBinding
    private lateinit var database : DatabaseReference
    private lateinit var personalList : ArrayList<Personal>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance().getReference("Personal")// refer to the tree Person
        personalList = arrayListOf()//************

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_applicant, container, false)


        //profilePicImageView = binding.profilePic as CircleImageView
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener() {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun fetchData(){
        database.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                personalList.clear()
                if(snapshot.exists()) {
                    for (personalSnap in snapshot.children) {
                        val person = personalSnap.getValue(Personal::class.java)
                        personalList.add(person!!)
                    }

                    val adapter = ViewApplicantAdapter()
                    adapter.setData(personalList)
                    recyclerView.adapter = adapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }


}