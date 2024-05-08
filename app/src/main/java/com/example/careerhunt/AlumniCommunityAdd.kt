package com.example.careerhunt

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.careerhunt.data.AlumniCommunityViewModel
import com.example.careerhunt.data.Alumni_community
import com.example.careerhunt.data.Personal
import com.example.careerhunt.data.PersonalViewModal
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.time.LocalDate

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlumniCommunityAdd.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlumniCommunityAdd : Fragment() {

    private lateinit var alumniCommunityViewModal : AlumniCommunityViewModel
    private lateinit var personalViewModel: PersonalViewModal
    val database = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").reference


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_alumni_community_add, container, false)

        val btnPost : Button = view.findViewById(R.id.btnPost)

        btnPost.setOnClickListener(){
            val etTitle   : EditText = view.findViewById(R.id.etTitle)
            val etContent : EditText = view.findViewById(R.id.etContent)


            //hardcoded create a records -> have to try 2nd time
            //personalViewModel = ViewModelProvider(this).get(PersonalViewModal::class.java)
            //val personal = Personal(0, "Personal01", "personal@gmail.com", "123", "UTAR", "", "0111-123456", "male", "College", "High")
            //personalViewModel.addPersonal(personal)

            alumniCommunityViewModal = ViewModelProvider(this).get(AlumniCommunityViewModel::class.java)
            //temp solution: should be personal ID of logined account of device
            //val alumniComm = Alumni_community(0, etTitle.text.toString(), etContent.text.toString(), LocalDate.now().toString(), 1)
            //alumniCommunityViewModal.addAlumniCommunity(alumniComm)

            val alumni_post = com.example.careerhunt.data.Alumni(etTitle.text.toString(), etContent.text.toString(), "2034-5-3", "user1")
            database.child("Alumni").push().setValue(alumni_post)

            Toast.makeText(requireContext(), "Post successful", Toast.LENGTH_LONG).show()
            val fragment = Alumni()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }


}