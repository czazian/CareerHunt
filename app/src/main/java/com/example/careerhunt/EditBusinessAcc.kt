package com.example.careerhunt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.careerhunt.databinding.FragmentEditBusinessAccBinding
import de.hdodenhof.circleimageview.CircleImageView


class EditBusinessAcc : Fragment() {
   private lateinit var binding : FragmentEditBusinessAccBinding
    private lateinit var profilePicImageView: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBusinessAccBinding.inflate(inflater, container, false)
        val view = binding.root

        //profilePicImageView = binding.profilePic as CircleImageView
        binding.btnBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        return view

    }

   /* private fun checkPermissionsAndOpenImagePicker() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        TedImagePicker.with(this)
            .start { uri ->
                // Handle the selected image URI
                uri?.let { updateProfilePicture(it) }
            }
    }

    private fun updateProfilePicture(imageUri: Uri) {
        // Update the profile picture ImageView with the selected image
        profilePicImageView.setImageURI(imageUri)
        // You can also save the URI or the image itself to use it later
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Permission denied. Cannot access storage.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }*/

}