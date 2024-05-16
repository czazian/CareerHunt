package com.example.careerhunt


import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.careerhunt.data.Apply_Job
import com.example.careerhunt.data.Bookmark
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Job
import com.example.careerhunt.dataAdapter.ImageAdapter
import com.example.careerhunt.databinding.FragmentJobDetailBinding
import com.google.android.gms.tasks.Tasks
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class JobDetail : Fragment() {

    private lateinit var binding: FragmentJobDetailBinding
    private lateinit var dialogToDisplay: View
    private lateinit var uploadDialog: Dialog
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var companyInfo: Company
    private lateinit var sharedUserTypePreferences: SharedPreferences
    private lateinit var sharedIDPreferences: SharedPreferences

    private var lastID: Long = 0
    private var jobID: String = ""
    private var personalID: Long = 0
    private var userId: String = ""
    private var userType: String = ""
    private var thisBookmark: Bookmark? = null
    private var existBookMarkID: String? = ""
    private lateinit var imageRecyclerView: RecyclerView
    private var imageList: ArrayList<String> = arrayListOf()
    private var totalImages: Int = 0
    private var loadedImages: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJobDetailBinding.inflate(layoutInflater, container, false)

        storageRef = FirebaseStorage.getInstance().getReference()

        //Get UserID & UserType
        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        userId = sharedIDPreferences.getString("userid","") ?: ""
        sharedUserTypePreferences = requireContext().getSharedPreferences("userType", Context.MODE_PRIVATE)
        userType = sharedUserTypePreferences.getString("userType","") ?: ""

//        userType = "Personal"
//        userId = "1"


        checkBookmarkExist()
        getImages()

        if (userType == "Company") {
            binding.btnApply.visibility = View.GONE
            val layoutParams = LinearLayout.LayoutParams(50, LinearLayout.LayoutParams.WRAP_CONTENT)
            binding.btnApply.layoutParams = layoutParams
            binding.detailBottom.layoutParams.height = 0
            binding.btnSave.visibility = View.INVISIBLE
            binding.btnSave.isClickable = false
        }


        //Database initialization
        dbRef = FirebaseDatabase.getInstance().getReference()
        storageRef = FirebaseStorage.getInstance().getReference()

        //get job object
        val bundle = arguments
        val job = bundle!!.getSerializable("job") as? Job

        //get company information from job object company id
        getCompanyInfoByID(job!!.companyID.toString())


        binding.jobCat.text = job!!.jobCategory.toString()
        binding.jobTy.text = job!!.jobType.toString()
        binding.jobTi.text = job!!.jobName.toString()
        binding.jobLoca.text =
            job!!.jobLocationState.toString() + ", " + job!!.jobLocationCity.toString()

        if (job!!.jobType.toString() == "Full Time") {
            binding.lblSalaryResult.text = job!!.jobSalary.toString() + "/Month"
        } else {
            binding.lblSalaryResult.text = job!!.jobSalary.toString() + "/Hour"
        }

        binding.lvlPostDateResult.text = job!!.jobPostedDate.toString()
        binding.descResult.text = job!!.jobDescription.toString()


        //Get JobID
        jobID = job!!.jobID.toString()


        //Back Button
        binding.btnBack.setOnClickListener() {
            //Get to know previous fragment
            getFragmentManager()?.popBackStackImmediate()
        }


        //When user clicks on the apply button
        binding.btnApply.setOnClickListener() {
            dialogToDisplay =
                LayoutInflater.from(this.requireContext()).inflate(R.layout.upload_resume, null)
            uploadDialog = AlertDialog.Builder(this.requireContext(), R.style.CustomAlertDialog)
                .setView(dialogToDisplay)
                .create()
            uploadDialog.show()

            val uploadButton = dialogToDisplay.findViewById<Button>(R.id.btnSelectFile)
            uploadButton.setOnClickListener() {
                val intent = Intent()
                    .setType("application/pdf")
                    .setAction(Intent.ACTION_GET_CONTENT)

                //A custom request code of 100
                Toast.makeText(requireContext(), "Get Image", Toast.LENGTH_LONG)
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 100)
            }

            val cancelButton = dialogToDisplay.findViewById<ImageButton>(R.id.cancelUploadButton)
            cancelButton.setOnClickListener() {
                uploadDialog.dismiss()
            }
        }


        //View Company location
        binding.btnCheckLocation.setOnClickListener() {
            val companyAddress = companyInfo.compAddress
            val uri = Uri.parse("geo:0,0?q=$companyAddress")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        //Working on bookmark adding
        binding.btnSave.setOnClickListener() {
            checkBookmarkExist()

            if (thisBookmark == null) {
                //Perform add bookmark action
                val bookmark = Bookmark(jobID, userId.toLong())

                //Insert into Firebase with Last ID + 1
                dbRef = FirebaseDatabase.getInstance().getReference("Bookmark")
                dbRef.push().setValue(bookmark)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Bookmark added",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Bookmark has fail inserted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                //Perform remove bookmark action
                dbRef = FirebaseDatabase.getInstance().getReference("Bookmark")

                Log.e("TAG", "existBookMarkID = $existBookMarkID")
                val query = dbRef.orderByKey().equalTo(existBookMarkID!!.toString())
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.ref.child(existBookMarkID!!.toString()).removeValue()
                            Toast.makeText(
                                requireContext(),
                                "Bookmark removed",
                                Toast.LENGTH_SHORT
                            ).show()

                            //Removed, reset it
                            thisBookmark = null
                            existBookMarkID = ""
                            binding.btnSave.setImageResource(R.drawable.baseline_bookmark_border_24)

                        } else {
                            Log.e("TAG", "Bookmark not found")
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("onCancelled", databaseError.toException().toString())
                    }
                })
            }
        }

        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //val selectedFile = data?.data // The URI with the location of the file
            val filename: String = getFilename(data)

            // Update the display filename
            val fileNameTextView = dialogToDisplay.findViewById<TextView>(R.id.fileName)
            fileNameTextView.text = filename
            fileNameTextView.setBackgroundResource(R.drawable.border)
            dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume).isClickable = true
            dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume)
                .setBackgroundColor(resources.getColor(R.color.uploadResumeBtn))


            //Confirm to submit the resume
            val submitBtn = dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume)
            submitBtn.setOnClickListener() {
                if (data?.data != null) {
                    //Get Count of Column -> For Last ID
                    dbRef = FirebaseDatabase.getInstance().getReference("Apply_Job")
                    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                lastID = snapshot.childrenCount

                                //Create a record in Apply_Job
                                //Create Object
                                val applyjob = Apply_Job(
                                    (lastID + 1),
                                    jobID.toString(),
                                    userId.toLong()
                                )

                                //Insert into Firebase with Last ID + 1
                                dbRef.child((lastID + 1).toString()).setValue(applyjob)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Job has inserted successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Job has fail inserted!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                //Define the file name and location -> Should define the Apply_Job ID as the reference key
                                val ref = storageRef.child("Resume/" + (lastID + 1) + ".pdf")

                                //Insert file into firebase storage
                                ref.putFile(data.data!!)
                                    .addOnCompleteListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Resume has been uploaded successfully.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        uploadDialog.dismiss()
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG)
                                .show()
                        }
                    })

                } else {
                    Toast.makeText(requireContext(), "Please select a resume.", Toast.LENGTH_SHORT)
                        .show()
                    dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume).isClickable = false
                    dialogToDisplay.findViewById<Button>(R.id.btnSubmitResume)
                        .setBackgroundColor(resources.getColor(R.color.lightGrey))
                }
            }

        }
    }

    fun getFilename(returnIntent: Intent?): String {
        var filename = ""

        returnIntent?.data?.let { returnUri ->
            requireContext().contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            filename = cursor.getString(nameIndex)
        }

        return filename
    }


    private fun checkBookmarkExist() {
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
                        type = bmSnapshot.child("userType").value.toString()
                        Log.e("Bookmark STATUS (jobId)=", jobId)
                        Log.e("Current STATUS (jobId)=", jobID.toString())
                        Log.e("Bookmark STATUS (userID)=", userID)
                        Log.e("Current STATUS (userID)=", userId)


                        if (userID == userId && jobID.toString() == jobId) {
                            Log.e("Bookmark STATUS =", "Bookmark is exist with ID <$bookmarkID>")
                            thisBookmark = Bookmark(jobId, userID.toLong())
                            existBookMarkID = bookmarkID

                            binding.btnSave.setImageResource(R.drawable.baseline_bookmark_24)

                            break
                        } else {
                            Log.e("Bookmark STATUS =", "Bookmark is not exist")
                            thisBookmark = null

                            binding.btnSave.setImageResource(R.drawable.baseline_bookmark_border_24)
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR", error.toString())
            }
        })
    }


    private fun getCompanyInfoByID(cmpId: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("Company")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (companySnapshot in snapshot.children) {
                        val userID = companySnapshot.key
                        if (userID.equals(cmpId)) {
                            companyInfo = companySnapshot.getValue(Company::class.java)!!
                        }
                    }

                    //Update company name
                    binding.companyAddressResl.text = companyInfo.compAddress
                    binding.companyPhoneResl.text = companyInfo.compPhoneNum
                    binding.comName.text = companyInfo.compName

                    //Update image company
                    storageRef = FirebaseStorage.getInstance().getReference()
                    val ref = storageRef.child("compProfile")
                        .child(companyInfo.companyID.toString() + ".png")
                    ref.downloadUrl
                        .addOnCompleteListener {
                            Glide.with(binding.comIcon)
                                .load(it.result.toString())
                                .into(binding.comIcon)
                        }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ERROR", error.toString())
            }
        })
    }

    private fun setupImageRecyclerView() {

        imageRecyclerView = binding.carouselRecyclerView
        CarouselSnapHelper().attachToRecyclerView(imageRecyclerView)
        imageRecyclerView.adapter = ImageAdapter(imageList = imageList)
        Log.e("TAG", "=> setupImageRecyclerView()")

    }

    private fun getImages() {
        val ref = storageRef.child("compImage")
        ref.listAll()
            .addOnSuccessListener { listResult ->
                totalImages = listResult.items.count { item ->
                    val imageName = item.name
                    val parts = imageName.split("|")
                    val jobIDFromFB = parts[0]
                    jobIDFromFB == jobID
                }

                if (totalImages == 0) {
                    val dynamicTextview = TextView(requireContext())
                    dynamicTextview.text = "No photo has been uploaded to this job."
                    binding.noPhtLayout.addView(dynamicTextview)
                    binding.carouselRecyclerView.layoutParams.height = 0
                } else {

                    // Iterate through each item (image) in the list
                    listResult.items.forEach { item ->
                        val imageName = item.name
                        val parts = imageName.split("|")
                        val jobIDFromFB = parts[0]
                        if (jobIDFromFB == jobID) {
                            Log.e("Image Get", "Image Get => $jobIDFromFB")

                            item.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                Log.e("URL", "URL GET = $imageUrl")
                                imageList.add(imageUrl)

                                loadedImages++
                                if (loadedImages == totalImages) {
                                    setupImageRecyclerView()
                                }
                            }
                        }
                    }
                }

            }

    }
}