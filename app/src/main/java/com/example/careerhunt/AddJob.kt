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
import com.example.careerhunt.data.Job
import com.example.careerhunt.databinding.FragmentAddJobBinding
import com.example.careerhunt.viewModel.JobViewModel
import java.text.SimpleDateFormat
import java.util.Date

class AddJob : Fragment() {

    private lateinit var binding: FragmentAddJobBinding
    private lateinit var jobViewModel: JobViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddJobBinding
            .inflate(layoutInflater, container, false)


        //TESTING - HARDCODED COMPANY ID
        var companyID: Int = 1



        //Cancel Button
        binding.btnCancel.setOnClickListener() {
            val fragment = JobListing()
            val transaction = activity?.supportFragmentManager?.beginTransaction()

            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }


        binding.spnState?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

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
                    else -> resources.getStringArray(R.array.kualaLumpur_city)
                }

                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayCity)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spnCity.adapter = adapter

            }

        }


        //Insert
        jobViewModel = ViewModelProvider(this).get(JobViewModel::class.java)

        binding.submitBtn.setOnClickListener() {
            //Get all input values
            val jobName = binding.txtJobTitle.text.toString()
            val jobLocation =
                binding.spnState.selectedItem.toString() + ", " + binding.spnCity.selectedItem.toString()
            val jobCategory = binding.ddlCategory.selectedItem.toString()
            val jobType: String = when (binding.radioGroup.checkedRadioButtonId) {
                R.id.rbFull -> "Full Time"
                R.id.rbPart -> "Part Time"
                else -> "Invalid"
            }
            val jobSalaryText = binding.txtSalary.text.toString()
            val jobSalary = jobSalaryText.toDoubleOrNull() ?: 0.0
            val jobDesc = binding.txtDescription.text.toString()
            val dateFormat = SimpleDateFormat("dd/M/yyyy")
            val postedDate = dateFormat.format(Date())

            if (!jobName.isNullOrEmpty() && jobType != "Invalid" && jobSalary != 0.0 && !jobDesc.isNullOrEmpty()) {

                //Create Object
                val job = Job(
                    0,
                    jobName,
                    jobLocation,
                    jobCategory,
                    jobType,
                    jobSalary,
                    jobDesc,
                    postedDate,
                    companyID
                )

                jobViewModel.addJob(job)
                Toast.makeText(
                    requireContext(),
                    "Job has inserted successfully.",
                    Toast.LENGTH_SHORT
                )

                //Redirect back to job listing
                val fragment = JobListing()
                val transaction = activity?.supportFragmentManager?.beginTransaction()

                transaction?.replace(R.id.frameLayout, fragment)
                transaction?.addToBackStack(null)
                transaction?.commit()

            } else {
                var emptyMessage = ""
                if(jobName.isNullOrEmpty()) {
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tName"
                }
                if(jobType == "Invalid") {
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tJob Type"
                }
                if(jobSalary == 0.0) {
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tJob Salary"
                }
                if(jobDesc.isNullOrEmpty()){
                    emptyMessage += "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tJob Description"
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