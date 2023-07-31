package com.example.spendease.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.spendease.Model.TransactionData
import com.example.spendease.R
import com.example.spendease.databinding.FragmentAllTransactionsBinding
import org.eazegraph.lib.charts.PieChart

class AllTransactions : Fragment() {
    private lateinit var binding: FragmentAllTransactionsBinding
    lateinit var pieChart : PieChart
    private var totalexpense = 0.0f
    private var totalGoal = 5000.0f
    private var totalFood = 0.0f
    private var totalShopping = 0.0f
    private var totalEducation = 0.0f
    private var totalOthers = 0.0f
    private var totalTransport = 0.0f
    private var totalHealth = 0.0f
    lateinit var UserDetails : SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = FragmentAllTransactionsBinding.inflate(inflater,container,false)
        yearSelector()
        getData()
        showAllTransactions()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    private fun yearSelector(){
        val yearslist = ArrayList<Int>()
        val startyear = 2020
        val endyear = 2030
        for (i in startyear..endyear){
            yearslist.add(i)
        }
        val yearspinneradapter = ArrayAdapter(requireContext(), com.airbnb.lottie.R.layout.support_simple_spinner_dropdown_item,yearslist)
        binding.yearlyspinner.adapter = yearspinneradapter
    }

    private fun showAllTransactions(){
        val transactionList = mutableListOf<TransactionData>()
        binding.transactionrecyclerview.visibility = View.VISIBLE
        binding.yearlyspinner.visibility = View.GONE
        binding.montlyselector.visibility = View.GONE
        binding.mainCard.visibility = View.GONE
        binding.textView4.visibility = View.GONE
        binding.title.text = "All Transactions"
        if(transactionList.isEmpty()){
            binding.transactionrecyclerview.visibility = View.GONE
            binding.noTransactionsDoneText.text = "No Transaction Done Yet!"
        }
        else{

        }
    }
    private fun getData(){
        UserDetails = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        binding.budgettv.text = UserDetails.getString("MonthlyBudget","")

    }

}