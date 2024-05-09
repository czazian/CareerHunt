package com.example.careerhunt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentViewAppliedJobBinding

class ViewAppliedJob : Fragment() {
   private lateinit var binding:FragmentViewAppliedJobBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewAppliedJobBinding.inflate(inflater, container, false)
        val view = binding.root

        //profilePicImageView = binding.profilePic as CircleImageView
        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        return view
    }

}