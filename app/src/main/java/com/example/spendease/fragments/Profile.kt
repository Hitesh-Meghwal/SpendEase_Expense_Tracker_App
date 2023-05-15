package com.example.spendease.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.spendease.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Profile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val editbtn = view.findViewById<FloatingActionButton>(R.id.editfab)
        editbtn.setOnClickListener {
            editProfile()
        }
        return view

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