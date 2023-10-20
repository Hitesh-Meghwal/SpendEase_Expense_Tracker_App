package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.spendease.R
import com.example.spendease.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class Profile : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val CAPTURE_REQ_CODE = 100
    private val GALLERY_REQ_CODE = 200
    lateinit var firebase: FirebaseFirestore
    lateinit var userDetails: SharedPreferences
    lateinit var profiledialog : BottomSheetDialog
    lateinit var selectedImg : Uri
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.profileimg.setOnClickListener {
            profileUpload()
        }
        firebase = FirebaseFirestore.getInstance()
        userDetails = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
        val setimg = userDetails.getString("UserImage","")
        if (setimg != null){
            Picasso.get().load(setimg).into(binding.profileimg)
        }
        monthYearBudget()
        return binding.root
    }

    @SuppressLint("IntentReset")
    private fun profileUpload(){
        profiledialog = BottomSheetDialog(requireContext(),R.style.bottom_dialog)
        profiledialog.setContentView(R.layout.dialog_update_user_profile)
        profiledialog.show()
        val capturebtn = profiledialog.findViewById<ImageView>(R.id.captureimg)
        val gallerybtn = profiledialog.findViewById<ImageView>(R.id.galleryimg)

        capturebtn?.setOnClickListener {
            //taking permission for camera access
            if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),CAPTURE_REQ_CODE)
            }
            else{
                val icapture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(icapture,CAPTURE_REQ_CODE)
                profiledialog.dismiss()
            }
        }

        gallerybtn?.setOnClickListener {
            val igallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            igallery.type = "image/*"
            startActivityForResult(Intent.createChooser(igallery,"Please select Image"),GALLERY_REQ_CODE)
            profiledialog.dismiss()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null){
            when(requestCode){
                CAPTURE_REQ_CODE -> {
                    val bitimg = data.extras?.get("data") as Bitmap
                    // Convert Bitmap to Uri
                    val capturedImageUri = getImageUriFromBitmap(requireContext(), bitimg)
                    uploadImageToFirebaseStorage(capturedImageUri)

                }
                GALLERY_REQ_CODE ->{
                    selectedImg = data.data!!
                    uploadImageToFirebaseStorage(selectedImg)
                }
            }
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitimg: Bitmap): Uri {
        val imgDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imgFile = File(imgDir,"Profile_img.jpg")
        try {
            val fileOutputStream = FileOutputStream(imgFile)
            bitimg.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream)
            fileOutputStream.close()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return FileProvider.getUriForFile(context,"${requireContext().packageName}.provider",imgFile)
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Profile Image Uploading...")
        progressDialog.setMessage("Please wait")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val storage = FirebaseStorage.getInstance()
        val uploader = storage.reference.child("profile_images").child(FirebaseAuth.getInstance().uid.toString())
        uploader.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val editor = userDetails.edit()
                    editor.putString("UserImage", imageUrl)
                    editor.apply()
                    Picasso.get().load(imageUrl).into(binding.profileimg)
                    Toast.makeText(requireContext(), "Profile Image Uploaded", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to Upload: ${e.message}", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }

    }

    private fun monthYearBudget() {
        val email = userDetails.getString("email","").toString()
        val name = userDetails.getString("Name","").toString()
        val monthlybudget = userDetails.getString("MonthlyBudget", "").toString()
        val yearlybudget = userDetails.getString("YearlyBudget", "").toString()
        binding.profilename.text = name
        binding.monthlybudgettv.text = monthlybudget
        binding.yearlybudgettv.text = yearlybudget
        binding.email.text = email
        binding.editfab.setOnClickListener {
            openEditDialog(name,monthlybudget,yearlybudget)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    @SuppressLint("ResourceType")
    private fun openEditDialog(name : String?, monthlybudget : String?, yearlybudget : String?){
        val bottomDialog = BottomSheetDialog(requireContext(),R.style.bottom_dialog)
        bottomDialog.setContentView(R.layout.fragment_update_user_details_dialog)

        val update = bottomDialog.findViewById<Button>(R.id.updatebtn)
        val cancel = bottomDialog.findViewById<Button>(R.id.cancelbtn)
        val nameEditor = bottomDialog.findViewById<TextInputEditText>(R.id.updatename)
        val monthlyEditor = bottomDialog.findViewById<TextInputEditText>(R.id.updatemonthlybudget)
        val yearlyEditor = bottomDialog.findViewById<TextInputEditText>(R.id.updateyearlybudget)

        nameEditor?.setText(name)
        monthlyEditor?.setText(monthlybudget)
        yearlyEditor?.setText(yearlybudget)

        update?.setOnClickListener {
            val name_ = nameEditor?.text.toString()
            val monthly_budget = monthlyEditor?.text.toString()
            val yearly_budget = yearlyEditor?.text.toString()

            if (name_.isEmpty() || monthly_budget.isEmpty() || yearly_budget.isEmpty()){
                Toast.makeText(requireContext(), "Name and Budget Cannot be Empty", Toast.LENGTH_SHORT).show()
            }
            else{
                userDetails = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
                val editor = userDetails.edit()
                editor.putString("Name", name_)
                editor.putString("MonthlyBudget", monthly_budget)
                editor.putString("YearlyBudget", yearly_budget)
                editor.apply()
                monthYearBudget()
                Toast.makeText(requireActivity(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                bottomDialog.dismiss()
            }
        }

        cancel?.setOnClickListener {
            bottomDialog.dismiss()
        }
        bottomDialog.show()
    }

}


