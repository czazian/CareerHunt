package com.example.careerhunt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.example.careerhunt.dataAdapter.ExpandableListAdapter
import com.example.careerhunt.databinding.FragmentFaqBinding


class faq : Fragment() {
    private lateinit var expandableListView: ExpandableListView
    private val header: MutableList<String> = ArrayList()
    private val body: MutableList<MutableList<String>> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFaqBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        expandableListView = binding.expandableListView

        val q1: MutableList<String> = ArrayList()
        q1.add("Lorem Ipsum is simply dummy text of the printing and typesetting industry. ")

        val q2: MutableList<String> = ArrayList()
        q2.add("Lorem Ipsum is simply dummy text of the printing and typesetting industry. ")

        val q3: MutableList<String> = ArrayList()
        q3.add("Lorem Ipsum is simply dummy text of the printing and typesetting industry. ")

        val q4: MutableList<String> = ArrayList()
        q4.add("Lorem Ipsum is simply dummy text of the printing and typesetting industry. ")

        header.add("1. How to apply job")
        header.add("2. Where to find suggested job")
        header.add("3. How to use interview prep")
        header.add("4. How to apply job")

        body.add(q1)
        body.add(q2)
        body.add(q3)
        body.add(q4)

        expandableListView.setAdapter(ExpandableListAdapter(requireContext(),expandableListView,header,body))

        return view
    }
}
