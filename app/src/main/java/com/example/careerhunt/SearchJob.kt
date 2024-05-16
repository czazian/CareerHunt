package com.example.careerhunt

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerhunt.data.Job
import com.example.careerhunt.dataAdapter.JobListingAdapter
import com.example.careerhunt.databinding.FragmentSearchJobBinding
import com.example.careerhunt.interfaces.JobInterface
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class SearchJob : Fragment(), JobInterface.RecyclerViewEvent, JobInterface.ProcessCompletionListener {
    private lateinit var binding: FragmentSearchJobBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var jobList: ArrayList<Job>
    private lateinit var dialogToDisplay: View
    private lateinit var dialog: Dialog
    private var totalImages: Int = 0
    private var loadedImages: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchJobBinding.inflate(layoutInflater, container, false)

        //Hide all things until finish loaded other components
        binding.itemCont.visibility = View.INVISIBLE

        //RecyclerView
        binding.searchRecyclerView.setNestedScrollingEnabled(false);

        binding.btnBack2.setOnClickListener() {
            val fragment = JobListing()

            val navigationView =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            navigationView.selectedItemId = R.id.home

            //Back to previous page with animation
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.setCustomAnimations(
                R.anim.fade_out,  // Enter animation
                R.anim.fade_in  // Exit animation
            )
            transaction?.addToBackStack(null)
            transaction?.commit()
        }


        //If from the Listing Page
        dbRef = FirebaseDatabase.getInstance().getReference("Job")
        jobList = arrayListOf()
        val from = arguments?.getString("from")
        val jobField = arguments?.getString("field")
        if (from == "latest") {
            val query = dbRef.orderByChild("jobPostedDate")
            getLatestData(query, "latest", "")
        } else if (from == "recommend") {
            val query = dbRef.orderByChild("jobCategory")
                .equalTo(jobField) //Later need to change to user job category
            getLatestData(query, "latest", "")
        } else {
            val query = dbRef
            getLatestData(query, "", "")
        }


        // Filtering
        binding.btnFilter.setOnClickListener() {

            Log.e("TAG", "btnFilter Clicked")


            dialogToDisplay =
                LayoutInflater.from(this.requireContext()).inflate(R.layout.bottom_sheet, null)
            showDialog()

            // Obtains input widgets
            val categorySpinner = dialogToDisplay.findViewById<Spinner>(R.id.category)
            val typeRadio = dialogToDisplay.findViewById<RadioGroup>(R.id.rdlGroup)
            val locationState = dialogToDisplay.findViewById<Spinner>(R.id.spinnerState)
            val locationCity = dialogToDisplay.findViewById<Spinner>(R.id.spinnerCity)
            val salaryMin = dialogToDisplay.findViewById<TextView>(R.id.min)
            val salaryMax = dialogToDisplay.findViewById<TextView>(R.id.max)

            locationState?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.e("TAG", "onItemSelected Clicked")
                    //Set City Spinner According to the State Selected
                    val arrayCity: Array<String> = when (locationState.selectedItem.toString()) {
                        "Johor" -> resources.getStringArray(R.array.johor_city)
                        "Kedah" -> resources.getStringArray(R.array.kedah_city)
                        "Kelantan" -> resources.getStringArray(R.array.kelantan_city)
                        "Malacca" -> resources.getStringArray(R.array.malacca_city)
                        "Negeri Sembilan" -> resources.getStringArray(R.array.nSembilan_city)
                        "Pahang" -> resources.getStringArray(R.array.pahang_city)
                        "Penang" -> resources.getStringArray(R.array.penang_city)
                        "Perak" -> resources.getStringArray(R.array.perak_city)
                        "Perlis" -> resources.getStringArray(R.array.perlis_city)
                        "Sabah" -> resources.getStringArray(R.array.sabah_city)
                        "Sarawak" -> resources.getStringArray(R.array.sarawak_city)
                        "Selangor" -> resources.getStringArray(R.array.selangor_city)
                        "Terengganu" -> resources.getStringArray(R.array.terengganu_city)
                        "Kuala Lumpur" -> resources.getStringArray(R.array.kualaLumpur_city)
                        else -> resources.getStringArray(R.array.nothing)
                    }

                    val adapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayCity)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    locationCity.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

            }

            val applyFiltering = dialogToDisplay.findViewById<Button>(R.id.ApplyBtn)
            applyFiltering.setOnClickListener() {


                binding.progressIndicatorSearch.show()
                binding.itemCont.visibility = View.INVISIBLE


                //Clear search box
                binding.txtSearch.text.clear()

                //Operations
                Log.e("TAG", "Apply Button Clicked")
                dialog.dismiss()
                val listerner = this

                val categorySpinnerValue: String? = categorySpinner.selectedItem.toString()
                val jobTypeValue: String? = when (typeRadio.checkedRadioButtonId) {
                    R.id.full -> "Full Time"
                    R.id.part -> "Part Time"
                    else -> ""
                }
                val locationStateValue: String?
                val locationCityValue: String?
                if(locationState.selectedItem == null){
                    locationStateValue = ""
                } else {
                    locationStateValue = locationState.selectedItem.toString()
                }
                if(locationCity.selectedItem == null){
                    locationCityValue= ""
                } else {
                    locationCityValue = locationCity.selectedItem.toString()
                }
                val salaryMinValue: Int? = salaryMin.text.toString().toIntOrNull()
                val salaryMaxValue: Int? = salaryMax.text.toString().toIntOrNull()

                dbRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        jobList.clear()

                        if (snapshot.exists()) {

                            for (jobSnapshot in snapshot.children) {
                                val job = jobSnapshot.getValue(Job::class.java)

                                if (job != null && meetsConditions(
                                        job,
                                        categorySpinnerValue,
                                        jobTypeValue,
                                        locationStateValue,
                                        locationCityValue,
                                        salaryMinValue,
                                        salaryMaxValue
                                    )
                                ) {
                                    jobList.add(job!!)
                                }
                            }

                            val adapter = JobListingAdapter(listerner, "recommend",  null, listerner)
                            adapter.setData(jobList)
                            binding.searchRecyclerView.adapter = adapter
                            binding.searchRecyclerView.layoutManager =
                                LinearLayoutManager(requireContext())
                            binding.searchRecyclerView.setHasFixedSize(true)
                            adapter.notifyDataSetChanged()

                            updateUIBasedOnDataCount()

                            //Set number of results
                            if (adapter.itemCount > 1) {
                                binding.lblResultNum.text =
                                    adapter.itemCount.toString() + " Results"
                            } else {
                                binding.lblResultNum.text =
                                    adapter.itemCount.toString() + " Result"
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG)
                            .show()
                    }
                })
            }
        }


        //Show result by searching
        binding.searchBtn.setOnClickListener()
        {
            binding.progressIndicatorSearch.show()
            binding.itemCont.visibility = View.INVISIBLE

            var searchKeyword: String = binding.txtSearch.text.toString()
            val query = dbRef.orderByKey()
            getLatestData(query, "search", searchKeyword)
        }


        return binding.root
    }

    fun imageLoaded() {

        loadedImages++
        Log.e("TAG", "On Image Loaded, as = $loadedImages")
        if (loadedImages == totalImages) {
            // All images are loaded
            onAllProcessesCompleted()
        }
    }

    private fun updateUIBasedOnDataCount() {
        // Check if recommend job has records
        loadedImages = 0
        totalImages = 0
        val count: Int = jobList.size

        if(count == 0){
            onAllProcessesCompleted()
            Log.e("TAG", "Count totalImage : $totalImages")
        } else {
            totalImages = count
            Log.e("TAG", "Count totalImage : $totalImages")
        }
    }

    fun meetsConditions(
        job: Job,
        jobCategoryValue: String?,
        typeRadioValue: String?,
        locationStateValue: String?,
        locationCityValue: String?,
        salaryMinValue: Int?,
        salaryMaxValue: Int?
    ): Boolean {
        // Initialize a variable to track whether all conditions are met
        var allConditionsMet = true

        // Check each condition one by one
        if (!jobCategoryValue.isNullOrEmpty()) {
            allConditionsMet = allConditionsMet && (job.jobCategory == jobCategoryValue)
        }
        if (!typeRadioValue.isNullOrEmpty()) {
            allConditionsMet = allConditionsMet && (job.jobType == typeRadioValue)
        }
        if (!locationStateValue.isNullOrEmpty()) {
            allConditionsMet = allConditionsMet && (job.jobLocationState == locationStateValue)
        }
        if (!locationCityValue.isNullOrEmpty()) {
            allConditionsMet = allConditionsMet && (job.jobLocationCity == locationCityValue)
        }
        if (salaryMinValue != null && salaryMaxValue == null){
            allConditionsMet = allConditionsMet && (job.jobSalary!! >= salaryMinValue.toString().toDouble())
        }
        if (salaryMinValue == null && salaryMaxValue != null){
            allConditionsMet = allConditionsMet && (job.jobSalary!! <= salaryMaxValue.toString().toDouble())
        }
        if (salaryMinValue != null && salaryMaxValue != null) {
            allConditionsMet = allConditionsMet && (job.jobSalary!! in salaryMinValue.toString().toDouble()..salaryMaxValue.toString().toDouble())
        }

        // Return true if all conditions are met, otherwise return false
        return allConditionsMet
    }

    private fun getLatestData(query: Query, from: String, searchKeyword: String) {
        //Get the listener
        val listener = this

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                jobList.clear()
                if (snapshot.exists()) {
                    if (from == "latest") {
                        binding.txtSearch.text.clear()
                        for (jobSnapshot in snapshot.children.reversed()) {
                            val job = jobSnapshot.getValue(Job::class.java)
                            jobList.add(job!!)
                        }
                    } else if (from == "recommend") {
                        binding.txtSearch.text.clear()
                        for (jobSnapshot in snapshot.children) {
                            val job = jobSnapshot.getValue(Job::class.java)
                            jobList.add(job!!)
                        }
                    } else if (from == "search") {
                        for (jobSnapshot in snapshot.children) {
                            val job = jobSnapshot.getValue(Job::class.java)
                            if (job != null && job.jobName!!.contains(
                                    searchKeyword,
                                    ignoreCase = true
                                )
                            ) {
                                jobList.add(job!!)
                            }
                        }
                    } else {
                        binding.txtSearch.text.clear()
                        for (jobSnapshot in snapshot.children) {
                            val job = jobSnapshot.getValue(Job::class.java)
                            jobList.add(job!!)
                        }
                    }
                    val adapter = JobListingAdapter(listener, "", null, listener)
                    adapter.setData(jobList)
                    binding.searchRecyclerView.adapter = adapter
                    binding.searchRecyclerView.layoutManager =
                        LinearLayoutManager(requireContext())
                    adapter.notifyDataSetChanged()

                    updateUIBasedOnDataCount()

                    //Set number of results
                    if (adapter.itemCount > 1) {
                        binding.lblResultNum.text = adapter.itemCount.toString() + " Results"
                    } else {
                        binding.lblResultNum.text = adapter.itemCount.toString() + " Result"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })

    }

     override fun onItemClick(position: Int, recyclerViewSource: String) {
        //Get the clicked object
        val jobObj: Job = jobList[position]
        val fragment = JobDetail()

        //Add Result Object into Bundle
        val bundle = Bundle()
        bundle.putSerializable("job", jobObj)
        fragment.arguments = bundle

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
        )
        transaction?.replace(R.id.frameLayout, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    fun showDialog() {
        dialogToDisplay =
            LayoutInflater.from(this.requireContext()).inflate(R.layout.bottom_sheet, null)
        dialog = AlertDialog.Builder(this.requireContext(), R.style.CustomAlertDialog)
            .setView(dialogToDisplay)
            .create()

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(R.drawable.bottom_sheet)
        dialog.window?.attributes?.windowAnimations = R.style.BottomSheetAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onAllProcessesCompleted() {
        Log.e("TAG", " SEARCH : onAllProcessesCompleted() Triggered")
        binding.progressIndicatorSearch.hide()
        binding.itemCont.visibility = View.VISIBLE
    }



}