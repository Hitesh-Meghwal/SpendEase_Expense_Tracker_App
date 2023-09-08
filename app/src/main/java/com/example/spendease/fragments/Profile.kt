package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
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
import com.example.spendease.R
import com.example.spendease.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class Profile : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private final val CAPTURE_REQ_CODE = 100
    private final val GALLERY_REQ_CODE = 200
    lateinit var firebase: FirebaseFirestore
    lateinit var userDetails: SharedPreferences
    lateinit var profiledialog : BottomSheetDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.progileimg.setOnClickListener {
            profileUpload()
        }
        firebase = FirebaseFirestore.getInstance()
        userDetails = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
        monthYearBudget()
        return binding.root
    }

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
            startActivityForResult(igallery,GALLERY_REQ_CODE)
            profiledialog.dismiss()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            when(requestCode){
                CAPTURE_REQ_CODE -> {
                    val bitimg = data?.extras?.get("data") as Bitmap
//                    if (bitimg != null) {
//                        val byteArrayOutputStream = ByteArrayOutputStream()
//                        bitimg.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//                        val imagedata = byteArrayOutputStream.toByteArray()
//
//                        firebase = FirebaseFirestore.getInstance()
//                        firebase.collection("Profile")
//                            .document(FirebaseAuth.getInstance().uid.toString())
//                            .collection("userProfile")
//                            .document()
//                            .set(mapOf("imageData" to imagedata))
//                            .addOnSuccessListener {
//                                binding.progileimg.setImageBitmap(bitimg)
//                                Toast.makeText(
//                                    requireContext(),
//                                    "Profile Set Successfully",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            .addOnFailureListener {
//                                Toast.makeText(
//                                    requireContext(),
//                                    "${it.message}",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                    }
                }
                GALLERY_REQ_CODE ->{
                    val selectedImg = data?.data
                    if (selectedImg != null){
                        val userProfile = hashMapOf<String, Any>("UserImage" to selectedImg.toString())
                        firebase.collection("Profile")
                            .document(FirebaseAuth.getInstance().uid.toString())
                            .collection("userProfile")
                            .add(userProfile)
                            .addOnSuccessListener {it->
                                val newImgId = it.id
                                val editor = userDetails.edit()
                                editor.putString("UserImageid",newImgId)
                                editor.putString("UserImage", selectedImg.toString())
                                editor.apply()
                                binding.progileimg.setImageURI(selectedImg)
                                Toast.makeText(requireContext(), "Image Uploaded", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Failed to Upload", Toast.LENGTH_SHORT).show()
                            }
                    }
                    else{
                        Toast.makeText(requireContext(), "Failed to Load...", Toast.LENGTH_SHORT).show()
                    }
                    profiledialog.dismiss()
                }
            }
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


