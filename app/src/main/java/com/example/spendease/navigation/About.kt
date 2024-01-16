package com.example.spendease.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spendease.R
import com.example.spendease.databinding.FragmentAboutBinding

@Suppress("UNREACHABLE_CODE")
class About : Fragment() {
    lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(inflater,container,false)

        binding.insta.setOnClickListener {
            openInstagram()
        }
        binding.github.setOnClickListener {
            openGithub()
        }

        return binding.root
    }

    private fun openInstagram(){
        val uri = Uri.parse("http://instagram.com/_u/hitesh_0.4")
        val intent = Intent(Intent.ACTION_VIEW,uri)
        intent.setPackage("com.instagram.android")
        try {
            startActivity(intent)
        }
        catch (e:ActivityNotFoundException){
            // Instagram app is not installed, open in web browser
            startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("http://instagram.com/_u/hitesh_0.4")))
        }
    }

    private fun openGithub(){
        val uri = Uri.parse("https://github.com/Hitesh-Meghwal")
        val intent = Intent(Intent.ACTION_VIEW,uri)
        try {
            startActivity(intent)
        }
        catch (e:ActivityNotFoundException){
//            GitHub app is not installed, open in web browser
            startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/Hitesh-Meghwal")))
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
        val bottomnav = requireActivity().findViewById<View>(R.id.bottomnavigation_id)
        bottomnav.visibility = View.GONE
    }

}