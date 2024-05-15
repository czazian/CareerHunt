package com.example.careerhunt

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Job
import com.example.careerhunt.data.Personal
import com.example.careerhunt.dataAdapter.JobListingAdapter
import com.example.careerhunt.databinding.FragmentJobListingBinding
import com.example.careerhunt.interfaces.JobInterface
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class JobListing : Fragment(), JobInterface.RecyclerViewEvent,
    JobInterface.ProcessCompletionListener {

    private lateinit var binding: FragmentJobListingBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var newJobList: ArrayList<Job>
    private lateinit var recommendedJobList: ArrayList<Job>
    private lateinit var userInfo: Personal
    private lateinit var companyInfo: Company
    private lateinit var sharedIDPreferences: SharedPreferences
    private lateinit var sharedUserTypePreferences: SharedPreferences
    private lateinit var jovViewLayout: View
    private var userId: String = ""
    private var userType: String = ""
    private var totalImages: Int = 0
    private var loadedImages: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        userId = sharedIDPreferences.getString("userid","") ?: ""
        sharedUserTypePreferences = requireContext().getSharedPreferences("userType", Context.MODE_PRIVATE)
        userType = sharedUserTypePreferences.getString("userType","") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobListingBinding.inflate(layoutInflater, container, false)        //Get UserID & UserType

        //Hide the fragment first
        binding.recommendedContainer.visibility = View.INVISIBLE
        binding.newPostedContainer.visibility = View.INVISIBLE


        Log.e("TAG", "User Type Preference = $userType")
        Log.e("TAG", "User ID Preference = $userId")

        //Hardcoded ID and userType
//        userId = "2"
//        userType = "Company"

        if (userType == "Personal") {
            getUserInfoBasedOnId()
            binding.btnAddJob.isVisible = false
        } else {
            getCompanyInfoByID()
            binding.noJobIndicator.isVisible = false
        }

        binding.recommendedJobRecyclerView.setNestedScrollingEnabled(false);
        binding.newJobRecyclerView.setNestedScrollingEnabled(false);



        //Go to add job
        binding.btnAddJob.setOnClickListener() {
            val fragment = AddJob()

            val bundle = Bundle()
            bundle.putString("companyID", companyInfo.companyID.toString())
            fragment.arguments = bundle

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }


        //View more - all
        binding.btnShowAllLatest.setOnClickListener() {

            val fragment = SearchJob()
            val bundle = Bundle()
            bundle.putString("from", "latest")
            fragment.arguments = bundle

            val navigationView =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            navigationView.selectedItemId = R.id.explore

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        //View more - recommended
        binding.btnShowAllRecommended.setOnClickListener() {
            if (binding.recommendedJobRecyclerView.adapter == null || binding.recommendedJobRecyclerView.adapter?.itemCount == 0) {
                Toast.makeText(
                    requireContext(),
                    "There is no job related to your field",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val fragment = SearchJob()

                if (userType == "Personal") {
                    val bundle = Bundle()
                    bundle.putString("from", "recommend")
                    bundle.putString("field", userInfo.jobField)
                    fragment.arguments = bundle
                }

                val navigationView =
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                navigationView.selectedItemId = R.id.explore

                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.frameLayout, fragment)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        }


        //Get the widgets from the layout file
        jovViewLayout =
            LayoutInflater.from(this.requireContext()).inflate(R.layout.job_viewholder, null)

        //Click on bookmark button
        val bookmarkButton = jovViewLayout.findViewById<ImageButton>(R.id.bookmarkBtn)
        bookmarkButton.setOnClickListener() {

        }



        //return view
        return binding.root
    }


    private fun fetchNewJobData() {

        //Get the listener
        val listener = this

        dbRef = FirebaseDatabase.getInstance().getReference("Job")
        val query = dbRef.orderByChild("jobPostedDate")
            .limitToLast(5) //Make a filter on the result from firebase
        newJobList = arrayListOf()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newJobList.clear()
                if (snapshot.exists()) {
                    for (jobSnapshot in snapshot.children.reversed()) {
                        val job = jobSnapshot.getValue(Job::class.java)
                        newJobList.add(job!!)
                    }
                    val adapter = JobListingAdapter(listener, "new", listener, null)
                    adapter.setData(newJobList)
                    binding.newJobRecyclerView.adapter = adapter
                    binding.newJobRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.newJobRecyclerView.setHasFixedSize(true)
                    adapter.notifyDataSetChanged()

                    updateUIBasedOnDataCount()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun fetchRecommendedJobData() {
        //Get the listener
        val listener = this

        //Get All Job related to User Job
        dbRef = FirebaseDatabase.getInstance().getReference("Job")

        var query: Query
        if (userType == "Personal") {
            Log.e("TAG", "User Field = " + userInfo.jobField)
            query = dbRef.orderByChild("jobCategory").equalTo(userInfo.jobField).limitToLast(5)

        } else {
            query = dbRef.orderByKey().limitToFirst(5)
        }

        recommendedJobList = arrayListOf()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recommendedJobList.clear()
                if (snapshot.exists()) {
                    for (jobSnapshot in snapshot.children.reversed()) {
                        val job = jobSnapshot.getValue(Job::class.java)
                        recommendedJobList.add(job!!)
                    }
                    val adapter = JobListingAdapter(listener, "recommend", listener, null)
                    adapter.setData(recommendedJobList)
                    binding.recommendedJobRecyclerView.adapter = adapter
                    binding.recommendedJobRecyclerView.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.recommendedJobRecyclerView.setHasFixedSize(true)
                    adapter.notifyDataSetChanged()

                    updateUIBasedOnDataCount()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }


        })
    }

    fun imageLoaded() {
        loadedImages++
        Log.e("TAG", "On Image Loaded, as = $loadedImages")
        if (loadedImages == totalImages) {
            // All images are loaded
            onAllProcessesCompleted()
        }
    }


    //On each RecyclerView Item clicked - Perform actual action here
    override fun onItemClick(position: Int, recyclerViewSource: String) {
        //Get the clicked object
        val jobObj: Job
        if (recyclerViewSource == "recommend") {
            jobObj = recommendedJobList[position]
        } else {
            jobObj = newJobList[position]
        }
        val fragment = JobDetail()

        //Add Result Object into Bundle
        val bundle = Bundle()
        bundle.putSerializable("job", jobObj)
        fragment.arguments = bundle

        //Go to desired fragment
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out
        )
        transaction?.replace(R.id.frameLayout, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }


    private fun getUserInfoBasedOnId() {
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        userId = sharedIDPreferences.getString("userid","") ?: ""
        sharedUserTypePreferences = requireContext().getSharedPreferences("userType", Context.MODE_PRIVATE)
        userType = sharedUserTypePreferences.getString("userType","") ?: ""


        val listener = this
        dbRef = FirebaseDatabase.getInstance().getReference("Personal")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (personalSnapshot in snapshot.children) {
                        val userID = personalSnapshot.key
                        if (userID.equals(userId)) {
                            userInfo = personalSnapshot.getValue(Personal::class.java)!!
                            Log.e(
                                "TAG",
                                "UserInfo Has MATCHING ID =$userID, then, UserInfo Value =$userInfo",
                            )
                            //Data Handling
                            fetchRecommendedJobData()
                            fetchNewJobData()

                            //Retrieve the user profile and update username
                            //Check length of username and make it smaller if too long
                            val words = userInfo.name
                            val wordsTrimmed = userInfo.name.trim()

                            if(wordsTrimmed.length > 12) {
                                binding.lblUserName.setTextSize(10.toString().toFloat())
                            }

                            binding.lblUserName.text = words

                            //Get image user
                            storageRef = FirebaseStorage.getInstance().getReference()
                            val ref = storageRef.child("imgProfile").child(userInfo.personalID.toString() + ".png")
                            ref.downloadUrl
                                .addOnCompleteListener {
                                    Glide.with(binding.profileImage)
                                        .load(it.result.toString())
                                        .into(binding.profileImage)
                                }

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getCompanyInfoByID() {

        val listener = this
        dbRef = FirebaseDatabase.getInstance().getReference("Company")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (companySnapshot in snapshot.children) {
                        val userID = companySnapshot.key
                        if (userID.equals(userId)) {
                            companyInfo = companySnapshot.getValue(Company::class.java)!!
                            //Data Handling
                            fetchRecommendedJobData()
                            fetchNewJobData()

                            //Retrieve the user profile and update username
                            //Check length of username and make it smaller if too long
                            val words = companyInfo.compName
                            val wordsTrimmed = companyInfo.compName.trim()

                            if(wordsTrimmed.length > 12) {
                                binding.lblUserName.setTextSize(17.toString().toFloat())
                            }

                            binding.lblUserName.text = words

                            //Get image company
                            storageRef = FirebaseStorage.getInstance().getReference()
                            val ref = storageRef.child("compProfile").child(companyInfo.companyID.toString() + ".png")
                            ref.downloadUrl
                                .addOnCompleteListener {
                                    Glide.with(binding.profileImage)
                                         .load(it.result.toString())
                                        .into(binding.profileImage)
                                }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun updateUIBasedOnDataCount() {
        //Reset
        loadedImages = 0
        totalImages = 0
        // Check if recommend job has records
        val countRecommended: Int = recommendedJobList.size
        val countNew: Int = newJobList.size
        totalImages = countNew + countRecommended
        Log.e("TAG", "Count for recommended job items : $countRecommended")
        Log.e("TAG", "Count for new job items : $countNew")
        Log.e("TAG", "Count totalImage : $totalImages")
        if (countRecommended == 0) {
            binding.btnShowAllRecommended.isClickable = false
            binding.noJobIndicator.visibility = View.VISIBLE
            binding.noJobIndicator.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            binding.noJobIndicator.visibility = View.GONE
            binding.noJobIndicator.height = 0
        }
    }

    override fun onAllProcessesCompleted() {
        Log.e("TAG", "onAllProcessesCompleted() Triggered")
        binding.progressIndicator.hide()
        binding.recommendedContainer.visibility = View.VISIBLE
        binding.newPostedContainer.visibility = View.VISIBLE
    }




}