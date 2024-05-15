package com.example.careerhunt

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerhunt.data.Bookmark
import com.example.careerhunt.data.Job
import com.example.careerhunt.dataAdapter.BookmarkAdapter
import com.example.careerhunt.databinding.FragmentBookmarkBinding
import com.example.careerhunt.interfaces.BookmarkInterface
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BookmarkListing : Fragment(), BookmarkInterface.ProcessCompletionListener,
    BookmarkInterface.RecyclerViewEvent {

    private lateinit var binding: FragmentBookmarkBinding
    private var userId: String = ""
    private var userType: String = ""
    private lateinit var sharedIDPreferences: SharedPreferences
    private lateinit var sharedUserTypePreferences: SharedPreferences

    private lateinit var dbRef: DatabaseReference
    private lateinit var bookmarkListingList: ArrayList<Bookmark>
    private var jobList: ArrayList<Job> = arrayListOf()
    private var totalImages: Int = 0
    private var loadedImages: Int = 0
    private var adapter: BookmarkAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(layoutInflater, container, false)

        //Hide all things until fragment finish loaded data from firebase
        binding.bookmarkItems.visibility = View.INVISIBLE

        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        userId = sharedIDPreferences.getString("userid", "") ?: ""
        sharedUserTypePreferences =
            requireContext().getSharedPreferences("userType", Context.MODE_PRIVATE)
        userType = sharedUserTypePreferences.getString("userType", "") ?: ""


        binding.bookmarkBack.setOnClickListener() {
            //Get to know previous fragment
            getFragmentManager()?.popBackStackImmediate()
        }


        dbRef = FirebaseDatabase.getInstance().getReference("Bookmark")
        bookmarkListingList = arrayListOf()
        val query = dbRef.orderByChild("userID").equalTo(userId.toDouble())


        //Get all records
        getLatestData(query)



        return binding.root
    }


    private fun getLatestData(query: Query) {
        //Get the listener
        val listener = this

        // Set up the adapter and layout manager once
        adapter = BookmarkAdapter(listener, listener)
        binding.bookMarkRecyclerView.adapter = adapter
        binding.bookMarkRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookmarkListingList.clear()
                if (snapshot.exists()) {

                    for (bmSnapshot in snapshot.children) {
                        val bookmark = bmSnapshot.getValue(Bookmark::class.java)
                        bookmarkListingList.add(bookmark!!)
                    }


                    // Update adapter data
                    jobList = adapter!!.setData(bookmarkListingList, userId.toLong())
                    adapter!!.notifyDataSetChanged()  // Notify the adapter that the data set has changed

                    //Assign jobList

                    view?.let {
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            val count = adapter!!.itemCount
                            Log.e("RecyclerView", "RecyclerView =" + count.toString())
                            updateUIBasedOnDataCount(count)
                        }
                    }

                } else {
                    onAllProcessesCompleted()
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Bookmark empty")
                        .setMessage("Sorry, your bookmark is currently empty, please add something into the bookmark and come back again!")
                        .setPositiveButton(resources.getString(R.string.sure)) { dialog, which ->
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
                        }.show()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })

    }



    override fun onItemClick(position: Int) {

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


    fun imageLoaded() {
        loadedImages++
        Log.e("TAG", "On Image Loaded, as = $loadedImages")
        if (loadedImages == totalImages) {
            // All images are loaded
            onAllProcessesCompleted()
        }
    }

    private fun updateUIBasedOnDataCount(count: Int) {
        // Check if recommend job has records
        loadedImages = 0
        totalImages = 0

        if (count == 0) {
            onAllProcessesCompleted()
            Log.e("TAG", "Count totalImage : $totalImages")
        } else {
            totalImages = count
            Log.e("TAG", "Count totalImage : $totalImages")
        }
    }

    override fun onAllProcessesCompleted() {
        Log.e("TAG", "Bookmark : onAllProcessesCompleted() Triggered")
        binding.progressIndicator.hide()
        binding.bookmarkItems.visibility = View.VISIBLE
    }

}

