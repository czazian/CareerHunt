package com.example.careerhunt

import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.careerhunt.data.Personal
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.time.LocalDate

class AlumniCommunityAdd : Fragment() {

    private lateinit var dbRef : DatabaseReference
    private lateinit var sharedIDPreferences : SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dbRef = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Alumni")

        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        val currentLoginPersonalId : String = sharedIDPreferences.getString("userid", "") ?: ""

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_alumni_community_add, container, false)

        val btnPost : Button = view.findViewById(R.id.btnPost)

        btnPost.setOnClickListener(){
            val etTitle   : EditText = view.findViewById(R.id.etTitle)
            val etContent : EditText = view.findViewById(R.id.etContent)
            val imgBtnBack : ImageButton = view.findViewById(R.id.imgBtnBack)

            val fragment = Alumni()
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            imgBtnBack.setOnClickListener(){
                transaction?.replace(R.id.frameLayout, fragment)
                transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }

            //temp solution: should be personal ID of logined account of device
            val alumni_post = com.example.careerhunt.data.Alumni("", etTitle.text.toString(), etContent.text.toString(), LocalDate.now().toString(), currentLoginPersonalId, arrayListOf(), arrayListOf())
            dbRef.push().setValue(alumni_post)

            Toast.makeText(requireContext(), "Post successful", Toast.LENGTH_LONG).show()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }


}