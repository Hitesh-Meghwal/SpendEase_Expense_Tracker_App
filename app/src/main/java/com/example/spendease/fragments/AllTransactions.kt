package com.example.spendease.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.spendease.R

class AllTransactions : Fragment() {
    lateinit var yearspinner : Spinner
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_transactions, container, false)
        yearspinner = view.findViewById(R.id.yearlyspinner)
        yearSelector()
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    private fun yearSelector(){
        val yearslist = ArrayList<Int>()
        val startyear = 2020
        val endyear = 2050
        for (i in startyear..endyear){
            yearslist.add(i)
        }
        val yearspinneradapter = ArrayAdapter(requireContext(), com.airbnb.lottie.R.layout.support_simple_spinner_dropdown_item,yearslist)
        yearspinner.adapter = yearspinneradapter
    }

}