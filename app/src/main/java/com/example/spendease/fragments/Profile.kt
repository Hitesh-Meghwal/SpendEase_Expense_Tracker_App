package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.spendease.R
import com.example.spendease.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText

class Profile : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private final val CAPTURE_REQ_CODE = 100
    private final val GALLERY_REQ_CODE = 200
    lateinit var userDetails: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.progileimg.setOnClickListener {
            profileUpload()
        }
        monthYearBudget()
        return binding.root
    }

    private fun profileUpload(){
        val profiledialog = BottomSheetDialog(requireContext(),R.style.bottom_dialog)
        profiledialog.setContentView(R.layout.dialog_update_user_profile)
        profiledialog.show()
        val capturebtn = profiledialog.findViewById<ImageView>(R.id.captureimg)
        val gallerybtn = profiledialog.findViewById<ImageView>(R.id.galleryimg)

        capturebtn?.setOnClickListener {
            val icapture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(icapture,CAPTURE_REQ_CODE)
        }

        gallerybtn?.setOnClickListener {
            val igallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(igallery,GALLERY_REQ_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK){
            when(requestCode){
                CAPTURE_REQ_CODE ->{
                    val bitimg = data?.extras?.get("data") as Bitmap
                    binding.progileimg.setImageBitmap(bitimg)
                }
                GALLERY_REQ_CODE ->{
                    binding.progileimg.setImageURI(data?.data)
                }
            }
        }
    }

    private fun monthYearBudget() {
        userDetails = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
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