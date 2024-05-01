package com.example.careerhunt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentViewPublishedJobBinding

class ViewPublishedJob : Fragment() {
    private lateinit var binding: FragmentViewPublishedJobBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPublishedJobBinding.inflate(inflater, container, false)
        val view = binding.root

        //profilePicImageView = binding.profilePic as CircleImageView
        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.btnClick.setOnClickListener(){
            // Navigate to View applicant fragment
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = ViewApplicant()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return view
    }

    fun onViewApplicantClick(view: View){
        // Navigate to View applicant fragment
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val newFragment = ViewApplicant()
        fragmentTransaction.replace(R.id.frameLayout, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}