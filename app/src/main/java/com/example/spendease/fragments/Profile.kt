package com.example.spendease.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.spendease.R
import com.example.spendease.databinding.FragmentProfileBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Profile : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    lateinit var userDetails: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.editfab.setOnClickListener {
            editProfile()
        }
        monthYearBudget()
        return binding.root

    }

    private fun monthYearBudget() {
        userDetails = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
        val monthlybudget = userDetails.getString("MonthlyBudget", "")
        val yearlybudget = userDetails.getString("YearlyBudget", "")
        binding.monthlybudgettv.text = monthlybudget
        binding.yearlybudgettv.text = yearlybudget
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun editProfile(){
        val action = ProfileDirections.actionProfileToUpdateUserDetailsDialog()
        findNavController().navigate(action)
    }


}