package com.example.careerhunt

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.careerhunt.data.Job
import com.example.careerhunt.dataAdapter.JobListingAdapter
import com.example.careerhunt.databinding.FragmentAddJobBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddJob : Fragment() {

    private lateinit var binding: FragmentAddJobBinding
    private var lastID: Long = 0
    private lateinit var dbRef : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddJobBinding
            .inflate(layoutInflater, container, false)


        //TESTING - HARDCODED COMPANY ID
        //var companyID: Int = 1

        //Get company ID
        val companyID = arguments?.getString("companyID")


        //Cancel Button
        binding.btnCancel.setOnClickListener() {
            val fragment = JobListing()
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }


        binding.spnState?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //Set City Spinner According to the State Selected
                val arrayCity: Array<String> = when (binding.spnState.selectedItem.toString()) {
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
                binding.spnCity.adapter = adapter

            }

        }


        //Get Count of Column -> For Last ID
        dbRef = FirebaseDatabase.getInstance().getReference("Job")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    lastID = snapshot.childrenCount
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })


        //Insert
        binding.submitBtn.setOnClickListener() {
            //Get all input values
            val jobName = binding.txtJobTitle.text.toString()
            val jobLocationState = binding.spnState.selectedItem.toString()
            val jobLocationCity = binding.spnCity.selectedItem.toString()
            val jobCategory = binding.ddlCategory.selectedItem.toString()

            val jobType: String = when (binding.radioGroupPF.checkedRadioButtonId) {
                R.id.rbFull -> "Full Time"
                R.id.rbPart -> "Part Time"
                else -> ""
            }

            val jobSalaryText = binding.txtSalary.text.toString()
            val jobSalary = jobSalaryText.toDoubleOrNull() ?: 0.0
            val jobDesc = binding.txtDescription.text.toString()

            val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val postedDate = dateFormat.format(Date())


            if (!jobName.isNullOrEmpty() && !jobType.isNullOrEmpty() && jobSalary != 0.0 && !jobDesc.isNullOrEmpty() && !jobCategory.isNullOrEmpty() && !jobLocationCity.isNullOrEmpty() && !jobLocationState.isNullOrEmpty()) {

                //Create Object
                val job = Job(
                    (lastID+1),
                    companyID!!.toInt(),
                    jobCategory,
                    jobDesc,
                    jobLocationState,
                    jobLocationCity,
                    jobName,
                    postedDate,
                    jobSalary,
                    jobType
                )

                //Insert into Firebase with Last ID + 1
                dbRef.child((lastID+1).toString()).setValue(job)
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


                //Redirect back to job listing
                //Get to know previous fragment
                getFragmentManager()?.popBackStackImmediate()

            } else {
                var emptyMessage = ""
                if(jobName.isNullOrEmpty()) {
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tName"
                }
                if(jobType.isNullOrEmpty()) {
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tJob Type"
                }
                if(jobSalary == 0.0) {
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tJob Salary"
                }
                if(jobDesc.isNullOrEmpty()){
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tJob Description"
                }
                if(jobCategory.isNullOrEmpty()){
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tJob Category"
                }
                if(jobLocationState.isNullOrEmpty()){
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tState"
                }
                if(jobLocationCity.isNullOrEmpty()){
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tCity"
                }

                //Show AlertDialog
                val errDialog = AlertDialog.Builder(requireContext())
                errDialog
                    .setMessage("Please ensure all fields are not empty.\n\nEmpty Fields : $emptyMessage")
                    .setCancelable(false)
                    .setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
                val alert = errDialog.create()
                alert.setTitle("Missing Input")
                alert.show()
            }

        }

        return binding.root
    }
}