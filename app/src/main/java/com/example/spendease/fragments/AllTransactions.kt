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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendease.Adapter.TransactionAdapter
import com.example.spendease.Model.TransactionData
import com.example.spendease.R
import com.example.spendease.databinding.FragmentAllTransactionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import org.eazegraph.lib.charts.PieChart

class AllTransactions : Fragment() {
    private lateinit var binding: FragmentAllTransactionsBinding
    lateinit var pieChart: PieChart
    private var totalexpense = 0.0f
    private var totalGoal = 5000.0f
    private var totalFood = 0.0f
    private var totalShopping = 0.0f
    private var totalEducation = 0.0f
    private var totalOthers = 0.0f
    private var totalTransport = 0.0f
    private var totalHealth = 0.0f
    lateinit var UserDetails: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentAllTransactionsBinding.inflate(inflater, container, false)
        yearSelector()
        getData()
        showAllTransactions()


//        when(binding.toggleselector.checkedButtonId){
//            R.id.all -> showAllTransactions()
//            R.id.monthly -> showMonthlyTransactions()
//            R.id.yearly -> shoeYearlyTransactions()
//        }
//
//        binding.toggleselector.addOnButtonCheckedListener{toggleSelector, checkId, isCheck->
//            if(isCheck){
//                when(checkId){
//                    R.id.all -> showAllTransactions()
//                    R.id.monthly -> showMonthlyTransactions()
//                    R.id.yearly -> shoeYearlyTransactions()
//                }
//            }
//        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    private fun yearSelector() {
        val yearslist = ArrayList<Int>()
        val startyear = 2020
        val endyear = 2030
        for (i in startyear..endyear) {
            yearslist.add(i)
        }
        val yearspinneradapter = ArrayAdapter(requireContext(), com.airbnb.lottie.R.layout.support_simple_spinner_dropdown_item, yearslist)
        binding.yearlyspinner.adapter = yearspinneradapter
    }

    private fun showAllTransactions() {
        binding.transactionrecyclerview.visibility = View.VISIBLE
        binding.yearlyspinner.visibility = View.GONE
        binding.montlyselector.visibility = View.GONE
        binding.mainCard.visibility = View.GONE
        binding.textView4.visibility = View.GONE
        val transactionList = mutableListOf<TransactionData>()
        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("Transactions")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("TransactionList")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val transaction = data.toObject(TransactionData::class.java)
                        transaction?.let {
                            transactionList.add(it)
                        }
                    }
                }
                if (transactionList.isEmpty()) {
                    binding.transactionrecyclerview.visibility = View.GONE
                    binding.noTransactionsDoneText.text = "No Transaction Done Yet!"
                    binding.noTransactionsDoneText.visibility = View.GONE
                } else {
                    val adapter = TransactionAdapter(requireContext(), "AllTransactions", transactionList)
                    binding.transactionrecyclerview.layoutManager = LinearLayoutManager(requireContext())
                    binding.transactionrecyclerview.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), ""+it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun showMonthlyTransactions() {

    }

    private fun shoeYearlyTransactions() {
        TODO("Not yet implemented")
    }

    private fun getData(){
        UserDetails = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        binding.budgettv.text = UserDetails.getString("MonthlyBudget","")

    }

}