package com.example.careerhunt.dataAdapter

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.careerhunt.JobListing
import com.example.careerhunt.R
import com.example.careerhunt.SearchJob
import com.example.careerhunt.data.Bookmark
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Job
import com.example.careerhunt.interfaces.JobInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.NonDisposableHandle.parent


//Need to add private val listener: JobViewModel.RecyclerViewEvent - Click on different items in the recyclerview
class JobListingAdapter(
    private var listener: JobInterface.RecyclerViewEvent,
    recyclerViewSource: String,
    private val fragmentListing: JobListing?,
    private val fragmentSearch: SearchJob?
) : RecyclerView.Adapter<JobListingAdapter.JobListingViewHolder>() {

    private var jobList = emptyList<Job>()
    private lateinit var storageRef: StorageReference
    private val recyclerViewSource: String = recyclerViewSource //From parameter
    private lateinit var companyInfo: Company
    private lateinit var dbRef: DatabaseReference
    private lateinit var sharedUserTypePreferences: SharedPreferences
    private lateinit var sharedIDPreferences: SharedPreferences

    private var thisBookmark: Bookmark? = null
    private var existBookMarkID: String? = ""
    private var userId: String = ""
    private var userType: String = ""


    inner class JobListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        //Company Details
        val lblCompanyName: TextView = itemView.findViewById(R.id.lblCompanyName)
        val companyImage: ImageView = itemView.findViewById(R.id.imgCompany)

        //Job Details
        val lblJobName: TextView = itemView.findViewById(R.id.lblJobTitle)
        val lblJobDescription: TextView = itemView.findViewById(R.id.lblDesc)
        val lblJobType: TextView = itemView.findViewById(R.id.lblType)
        val lblLocation: TextView = itemView.findViewById(R.id.lblLocation)


        //RecyclerViewInterface Members - Click on different items in the recyclerview
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobListingViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.job_viewholder, parent, false)

        storageRef = FirebaseStorage.getInstance().getReference()

        //Get UserID & UserType
        sharedIDPreferences = parent.context.getSharedPreferences("userid", Context.MODE_PRIVATE)
        userId = sharedIDPreferences.getString("userid","") ?: ""
        sharedUserTypePreferences = parent.context.getSharedPreferences("userType", Context.MODE_PRIVATE)
        userType = sharedUserTypePreferences.getString("userType","") ?: ""
        Log.e("TAG","User Type from onCreateViewHolder $userType")

//        userType = "Personal"
//        userId = "1"

        return JobListingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    //Bind Data from Database to View
    override fun onBindViewHolder(holder: JobListingViewHolder, position: Int) {
        //Get item clicked
        val currentItem = jobList[position]

        //If it is company account disable the bookmark
        Log.e("TAG","User Type from onBindViewHolder $userType")
        if(userType == "Company") {
            val bookmarkIcon = holder.itemView.findViewById<ImageButton>(R.id.bookmarkBtn)
            bookmarkIcon.visibility = View.INVISIBLE
        }

        //Check length of description and limit it
        val words = currentItem.jobDescription.toString()
        val wordsTrimmed = currentItem.jobDescription.toString().trim()

        val upToNCharacters: String = words.substring(0, Math.min(wordsTrimmed.length, 50))
        holder.lblJobDescription.text = "$upToNCharacters..."

        //Get image - company/user
        val ref = storageRef.child("compProfile").child(currentItem.companyID.toString() + ".png")
        ref.downloadUrl
            .addOnCompleteListener {
                Glide.with(holder.companyImage)
                    .load(it.result.toString())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.d("TAG", "OnResourceReady")
                            if (fragmentListing == null) {
                                fragmentSearch?.imageLoaded()
                            } else {
                                fragmentListing?.imageLoaded()
                            }
                            return false
                        }
                    })
                    .into(holder.companyImage)

                //Bind value
                holder.lblJobName.text = currentItem.jobName
                holder.lblJobType.text = currentItem.jobType
                holder.lblLocation.text = currentItem.jobLocationState + ", " + currentItem.jobLocationCity



                //Through foreign key to find company info, then apply it
                //Get comp info
                getCompanyInfoByID(currentItem.companyID.toString(), holder)

                //Working with Bookmark display
                checkBookmarkExist(currentItem.jobID.toString(), holder)

            }
    }

    private fun getCompanyInfoByID(cmpId: String, holder: JobListingViewHolder) {

        dbRef = FirebaseDatabase.getInstance().getReference("Company")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (companySnapshot in snapshot.children) {
                        val userID = companySnapshot.key
                        if (userID.equals(cmpId)) {
                            companyInfo = companySnapshot.getValue(Company::class.java)!!

                            val companyName = holder.itemView.findViewById<TextView>(R.id.lblCompanyName)
                            companyName.text = companyInfo.compName
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR", error.toString())
            }
        })
    }


    private fun checkBookmarkExist(jobID: String, holder: JobListingViewHolder) {
        //GET all bookmarks first
        dbRef = FirebaseDatabase.getInstance().getReference("Bookmark")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var jobId = ""
                    var userID = ""
                    var type = ""
                    for (bmSnapshot in snapshot.children) {
                        val bookmarkID = bmSnapshot.key
                        jobId = bmSnapshot.child("jobID").value.toString()
                        userID = bmSnapshot.child("userID").value.toString()
                        Log.e("Bookmark STATUS (jobId)=", jobId)
                        Log.e("Current STATUS (jobId)=", jobID)
                        Log.e("Bookmark STATUS (userID)=", userID)
                        Log.e("Current STATUS (userID)=", userId)


                        if (userID == userId && jobID.toString() == jobId) {
                            Log.e("Bookmark STATUS =", "Bookmark is exist with ID <$bookmarkID>")

                            val bookmarkIcon = holder.itemView.findViewById<ImageButton>(R.id.bookmarkBtn)
                            bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24)
                            break
                        } else {
                            Log.e("Bookmark STATUS =", "Bookmark is not exist")

                            val bookmarkIcon = holder.itemView.findViewById<ImageButton>(R.id.bookmarkBtn)
                            bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_border_24)
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR", error.toString())
            }
        })
    }


    //Get all result from Fragment.kt, to allow get item one by one
    fun setData(jobList: List<Job>) {
        this.jobList = jobList
        notifyDataSetChanged()
    }
}