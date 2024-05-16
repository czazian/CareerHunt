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
import com.example.careerhunt.BookmarkListing
import com.example.careerhunt.R
import com.example.careerhunt.data.Bookmark
import com.example.careerhunt.data.Job
import com.example.careerhunt.interfaces.BookmarkInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class BookmarkAdapter(
    private var listener: BookmarkInterface.RecyclerViewEvent,
    private val fragment: BookmarkListing?
) :
    RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    private var jobList: ArrayList<Job> = arrayListOf()
    private var bookmarkList = emptyList<Bookmark>()
    private lateinit var storageRef: StorageReference
    private lateinit var dbRef: DatabaseReference

    inner class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        //Get details
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.job_viewholder, parent, false)

        storageRef = FirebaseStorage.getInstance().getReference()


        return BookmarkViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    //Bind Data from Database to View
    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {

        //Get item clicked
        val currentItem = jobList[position]
        Log.e("Current Item in Bookmark", jobList[position].toString())

        val words = currentItem.jobDescription.toString()
        val wordsTrimmed = currentItem.jobDescription.toString().trim()

        val upToNCharacters: String = words.substring(0, Math.min(wordsTrimmed.length, 50))
        holder.lblJobDescription.text = "$upToNCharacters..."

        holder.lblJobName.text = currentItem.jobName
        holder.lblJobType.text = currentItem.jobType
        holder.lblLocation.text = currentItem.jobLocationState + ", " + currentItem.jobLocationCity

        val bookmarkIcon = holder.itemView.findViewById<ImageButton>(R.id.bookmarkBtn)
        bookmarkIcon.visibility = View.INVISIBLE


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
                            fragment?.imageLoaded()
                            return false
                        }
                    })
                    .into(holder.companyImage)
            }
    }

    fun setData(bookmarkList: List<Bookmark>, currentUserID: Long): ArrayList<Job> {
        //Get UserID & UserType

        this.bookmarkList = bookmarkList
        jobList.clear()

        //Will be execute as many times as the bookmarkList length
        for (bookmark in bookmarkList) {
            val jobID = bookmark.jobID
            val userID = bookmark.userID
            if (userID == currentUserID) {
                dbRef = FirebaseDatabase.getInstance().getReference("Job")
                dbRef.child(jobID.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val job = snapshot.getValue(Job::class.java)
                            if (job != null) {
                                jobList.add(job)

                                Log.e("Item added", "Item added$job")
                                notifyDataSetChanged()  // Notify adapter after each addition
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("ERROR", error.toString())
                        }
                    })
            }
        }
        return jobList
    }
}