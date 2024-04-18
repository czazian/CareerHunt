package com.example.careerhunt

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.data.AlumniCommunity
import com.example.careerhunt.dataAdapter.AlumniCommunity_adapter
import java.text.SimpleDateFormat
import java.time.LocalDate

class Alumni : Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //display fragment alumni
        val view = inflater.inflate(R.layout.fragment_alumni, container, false)

        //default list
        val alumniCommunityLists : List<AlumniCommunity> = listOf(
            AlumniCommunity("John", "TARUMT", "Title 1", "A paragraph is defined as “a group of sentences or a single sentence that forms a unit” (Lunsford and Connors 116). Length and appearance do not determine whether a section in a paper is a paragraph. For instance, in some styles of writing, particularly journalistic styles, a paragraph can be just one sentence long.", LocalDate.parse("2023-01-17")),
            AlumniCommunity("Alex", "UTAR","Title 2", "Content 2", LocalDate.parse("2023-02-17")),
            AlumniCommunity("Mark", "TARUMT", "Title 3", "A paragraph is defined as “a group of sentences or a single sentence that forms a unit” (Lunsford and Connors 116). Length and appearance do not determine whether a section in a paper is a paragraph. For instance, in some styles of writing, particularly journalistic styles, a paragraph can be just one sentence long.", LocalDate.parse("2023-03-17")),
            AlumniCommunity("Steven", "NICE", "Title 4", "Content 4", LocalDate.parse("2023-04-17")),
        )
        val recyclerView: RecyclerView = view.findViewById(R.id.alumni_recycle_view)
        //past in
        recyclerView.adapter = AlumniCommunity_adapter(alumniCommunityLists)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        //val fragment = Alumni()
        //val transaction = activity?.supportFragmentManager?.beginTransaction()
        //transaction?.replace(R.id.frameLayout, fragment)
        //transaction?.addToBackStack(null)
        //transaction?.commit()

        return view
    }
}