package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendease.Adapter.TransactionAdapter
import com.example.spendease.Model.TransactionData
import com.example.spendease.R
import com.example.spendease.databinding.FragmentAllTransactionsBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.text.SimpleDateFormat
import java.util.Calendar

class AllTransactions : Fragment() {
    private lateinit var binding: FragmentAllTransactionsBinding
    private var month = " "
    private var year = 0
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
//                    R.id.yearly -> showYearlyTransactions()
//                }
//            }
//        }

        return binding.root
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
                    binding.noTransactionsDoneText.visibility = View.VISIBLE
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

    @SuppressLint("SimpleDateFormat")
    private fun showMonthlyTransactions() {
//        Taking year format
        year = SimpleDateFormat("YYYY").format(Calendar.getInstance().time).toInt()
        val list = mutableListOf<Int>()
        list.clear()
        for(i in year downTo 2022){
            list += i
        }
    }

    private fun showYearlyTransactions() {

    }

    private fun getData(){
        UserDetails = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        binding.budgettv.text = UserDetails.getString("MonthlyBudget","")

    }

    private fun showPiChart(){
        binding.piechart.clearChart()
        binding.piechart.addPieSlice(PieModel("Food",totalFood, ContextCompat.getColor(requireContext(),R.color.lightblue)))
        binding.piechart.addPieSlice(PieModel("Shopping", totalShopping, ContextCompat.getColor(requireContext(), R.color.blue)))
        binding.piechart.addPieSlice(PieModel("Transport", totalTransport, ContextCompat.getColor(requireContext(), R.color.yellow)))
        binding.piechart.addPieSlice(PieModel("Education", totalEducation, ContextCompat.getColor(requireContext(), R.color.lightBrown)))
        binding.piechart.addPieSlice(PieModel("Health", totalHealth, ContextCompat.getColor(requireContext(), R.color.green)))
        binding.piechart.addPieSlice(PieModel("Others", totalOthers, ContextCompat.getColor(requireContext(), R.color.red)))

        if(totalGoal>totalexpense){
            binding.piechart.addPieSlice(PieModel("Left",totalGoal-(totalexpense), ContextCompat.getColor(requireContext(), R.color.background_deep)))
        }
        binding.piechart.startAnimation()
    }

//    when user click one of the button then the button color
//    and all design is set by this function
    private fun setMonth(v:View,button: MaterialButton){
        month = button.text.toString()
        button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.purple_200))
        button.setStrokeColorResource(R.color.purple_200)
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))

        when(v){
            binding.january -> {
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.february -> {
                removeBackground(binding.january)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.march -> {
                removeBackground(binding.january)
                removeBackground(binding.february)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.april -> {
                removeBackground(binding.january)
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.may -> {
                removeBackground(binding.january)
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.june -> {
                removeBackground(binding.january)
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.july -> {
                removeBackground(binding.january)
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.august -> {
                removeBackground(binding.january)
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.september ->{
                removeBackground(binding.january)
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.october)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.october -> {
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.january)
                removeBackground(binding.november)
                removeBackground(binding.december)
            }
            binding.november -> {
                removeBackground(binding.january)
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.december)
            }
            binding.december -> {
                removeBackground(binding.january)
                removeBackground(binding.february)
                removeBackground(binding.march)
                removeBackground(binding.april)
                removeBackground(binding.may)
                removeBackground(binding.june)
                removeBackground(binding.july)
                removeBackground(binding.august)
                removeBackground(binding.september)
                removeBackground(binding.october)
                removeBackground(binding.november)
            }
        }
    }

    //    From multiple button , when you click a button
    //    than the button that are not click then background of
    //    that button is set by using this function
    private fun removeBackground(button: MaterialButton) {
        button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.transparent))
        button.setIconTintResource(R.color.textSecondary)
        button.setStrokeColorResource(R.color.textSecondary)
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.textSecondary))
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }
}