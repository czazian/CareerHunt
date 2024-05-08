package com.example.careerhunt

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
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
import com.example.careerhunt.data.AlumniCommunityViewModel
import com.example.careerhunt.data.Alumni_community
import com.example.careerhunt.data.Alumni_community_comment
import com.example.careerhunt.dataAdapter.AlumniCommunityComment_adapter
import com.example.careerhunt.dataAdapter.AlumniCommunity_adapter
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue


/**
 * A simple [Fragment] subclass.
 * Use the [AlumniCommunityDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlumniCommunityDetail : Fragment() {

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

        //sent comment button
        val btnCommentSent : ImageButton = view.findViewById(R.id.btnPost)

        lateinit var alumniCommunityViewModel: AlumniCommunityViewModel
        val postId = arguments?.getString("postId")

        //Post Upper detail load
        //id successfully past inside here
        alumniCommunityViewModel = ViewModelProvider(this).get(AlumniCommunityViewModel::class.java)

            alumniCommunityViewModel.findById(postId!!.toInt()).observe(viewLifecycleOwner, Observer { alumniDetail ->
                alumniDetail?.let{
                    tvUsername.text = it.id.toString()
                    tvSchool.text   = it.id.toString()
                    tvTitle.text   = it.title
                    tvContent.text   = it.content
                    tvDate.text   = calDay(it.posted_at)
                }

            })

        //Post bottom detail (comment) load
        val adapter = AlumniCommunityComment_adapter()
        val recyclerView: RecyclerView = view.findViewById(R.id.alumni_comment_recycle_view)
        val tvComment : TextView =  view.findViewById(R.id.tvComment)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        alumniCommunityViewModel = ViewModelProvider(this).get(AlumniCommunityViewModel::class.java)
        alumniCommunityViewModel.findCommentbyPostId(postId.toInt()).observe(viewLifecycleOwner, Observer {alumniCommunityCommentList->
            adapter.setData(alumniCommunityCommentList)

            if(adapter.itemCount >= 1){
                tvComment.text = adapter.itemCount.toString() + " Comment(s)"
            }else{
                tvComment.text = "No comment"
            }

        })


        //wait for whole database added first
        btnCommentSent.setOnClickListener(){
            var etComment   : EditText = view.findViewById(R.id.editText)

            lateinit var alumniCommunityViewModel: AlumniCommunityViewModel
            val postId = arguments?.getString("postId")
            alumniCommunityViewModel = ViewModelProvider(this).get(AlumniCommunityViewModel::class.java)

            val alumniCommComment = postId?.toInt()
                ?.let { it1 -> Alumni_community_comment(0, etComment.text.toString(), 1, it1) }
            if (alumniCommComment != null) {
                alumniCommunityViewModel.addAlumniCommunityComment(alumniCommComment)
            }

            //make it empty after sent
            etComment.setText("")
            Toast.makeText(requireContext(), "Post successful:" + etComment.text.toString(), Toast.LENGTH_LONG).show()
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment AlumniCommunityDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(arge_1: String) =
            AlumniCommunityDetail().apply {
                arguments = Bundle().apply {
                    putString("arge_1", arge_1)
                }
            }
    }
}