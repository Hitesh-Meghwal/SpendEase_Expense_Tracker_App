package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

class AllTransactions : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentAllTransactionsBinding
    private var month = " "
    private var year = 0
    private var monthInt = 1
    lateinit var pieChart: PieChart
    private var totalexpense = 0.0
    private var totalGoal = 5000.0f
    private var totalFood = 0.0f
    private var totalShopping = 0.0f
    private var totalEducation = 0.0f
    private var totalOthers = 0.0f
    private var totalTransport = 0.0f
    private var totalHealth = 0.0f
    lateinit var firebase: FirebaseFirestore
    lateinit var UserDetails: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentAllTransactionsBinding.inflate(inflater, container, false)
        UserDetails = requireActivity().getSharedPreferences("UserDetails",MODE_PRIVATE)
        firebase = FirebaseFirestore.getInstance()
        showAllTransactions()


        when(binding.toggleselector.checkedButtonId){
            R.id.all -> showAllTransactions()
            R.id.monthly -> showMonthlyTransactions()
            R.id.yearly -> showYearlyTransaction()
        }

        binding.toggleselector.addOnButtonCheckedListener{toggleSelector, checkId, isCheck->
            if(isCheck){
                when(checkId){
                    R.id.all -> showAllTransactions()
                    R.id.monthly -> showMonthlyTransactions()
                    R.id.yearly -> showYearlyTransaction()
                }
            }
        }
        setListener(binding)

        return binding.root
    }

    private fun showAllTransactions() {
        binding.transactionrecyclerview.visibility = View.VISIBLE
        binding.yearlyspinner.visibility = View.GONE
        binding.montlyselector.visibility = View.GONE
        binding.mainCard.visibility = View.GONE
        binding.textView4.visibility = View.GONE
        val transactionList = mutableListOf<TransactionData>()
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
                notifyUser("${it.message}")
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun yearSpinner(){
        // Taking current year
        year = SimpleDateFormat("YYYY").format(Calendar.getInstance().time).toInt()
        val list = mutableListOf<Int>()
        list.clear()
        for(i in year downTo 2022){
            list += i
        } // [2023,2020]
        val yearAdapter  = ArrayAdapter(requireContext(), com.airbnb.lottie.R.layout.support_simple_spinner_dropdown_item,list)
        binding.yearlyspinner.adapter = yearAdapter
    }

    private fun showMonthlyTransactions() {
        yearSpinner()
        setMonth(binding.january,binding.january)
        showMonthsTransaction()
        binding.transactionrecyclerview.visibility = View.VISIBLE
        binding.mainCard.visibility = View.VISIBLE
        binding.yearlyspinner.visibility = View.VISIBLE
        binding.textView4.visibility = View.VISIBLE
        binding.montlyselector.visibility = View.VISIBLE
        binding.title.text = "Monthly Transaction"
        binding.yearlyspinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                year = binding.yearlyspinner.selectedItem.toString().toInt()
                showMonthsTransaction()
            }
            //to close the onItemSelected
            override fun onNothingSelected(parent: AdapterView<*>?) {
                year = binding.yearlyspinner.selectedItem.toString().toInt()
                showMonthsTransaction()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun showMonthsTransaction(){
        pieChart = binding.piechart
        pieChart.clearChart()
        totalexpense = 0.0
        totalGoal = UserDetails.getString("MonthlyBudget","")?.toFloat()!!
        totalEducation = 0.0f
        totalFood = 0.0f
        totalHealth = 0.0f
        totalOthers = 0.0f
        totalShopping = 0.0f
        totalTransport = 0.0f
        val currency  = UserDetails.getString("Currency","")

        val transactionList = mutableListOf<TransactionData>()
        firebase.collection("Transactions")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("TransactionList")
            .whereEqualTo("month",monthInt)
            .whereEqualTo("year",year)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (data in it.documents){
                        val transaction = data.toObject<TransactionData>()
                        transaction?.let {
                            if (!transactionList.contains(it)){
                                transactionList.add(it)
                            }
                        }
                    }
                }
                if (transactionList.isEmpty()){
                    binding.noTransactionsDoneText.text = "No Transactions done on $month $year"
                    binding.noTransactionsDoneText.visibility = View.VISIBLE
                    binding.transactionrecyclerview.visibility = View.GONE
                    binding.mainCard.visibility = View.GONE
                    binding.textView4.visibility = View.GONE
                }
                else{
                    binding.noTransactionsDoneText.visibility = View.GONE
                    binding.transactionrecyclerview.visibility = View.VISIBLE
                    binding.textView4.visibility = View.VISIBLE
                    binding.mainCard.visibility = View.VISIBLE
                    val transactionAdapter  = TransactionAdapter(requireContext(),"AllTransaction",transactionList.reversed())
                    binding.transactionrecyclerview.layoutManager = LinearLayoutManager(requireContext())
                    binding.transactionrecyclerview.adapter = transactionAdapter
                    transactionAdapter.notifyDataSetChanged()
                }
                for (i in transactionList){
                    totalexpense += i.amount
                    when(i.category){
                        "Food"->{
                            totalFood += i.amount.toFloat()
                        }
                        "Education"->{
                            totalEducation += i.amount.toFloat()
                        }
                        "Shopping"->{
                            totalShopping += i.amount.toFloat()
                        }
                        "Health"->{
                            totalHealth += i.amount.toFloat()
                        }
                        "Transport"->{
                            totalTransport += i.amount.toFloat()
                        }
                        "Others"->{
                            totalOthers += i.amount.toFloat()
                        }
                    }
                }
                binding.budgettv.text = "$currency ${totalGoal.toInt()}"
                binding.expensetv.text = "$currency ${totalexpense.toInt()}"
                binding.datetv.text = "$month $year"
                if (totalexpense > totalGoal){
                    binding.indicator.setImageResource(R.drawable.ic_negative_transaction)
                    binding.expensetv.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
                }
                else{
                    binding.indicator.setImageResource(R.drawable.ic_positive_amount)
                }
                showPiChart()
            }
            .addOnFailureListener {
                notifyUser("${it.message}")
            }
    }

    private fun showYearlyTransaction() {
        binding.title.text = "Yearly Transactions"
        binding.monthtext.text = "Yearly Budget"
        yearSpinner()
        binding.transactionrecyclerview.visibility = View.VISIBLE
        binding.mainCard.visibility = View.VISIBLE
        binding.yearlyspinner.visibility = View.VISIBLE
        binding.textView4.visibility = View.VISIBLE
        binding.montlyselector.visibility = View.GONE
        showYearlyTransactions()
        binding.yearlyspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               year = binding.yearlyspinner.selectedItem.toString().toInt()
                showYearlyTransactions()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                year = binding.yearlyspinner.selectedItem.toString().toInt()
                showYearlyTransactions()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun showYearlyTransactions(){
        pieChart = binding.piechart
        pieChart.clearChart()
        totalexpense = 0.0
        totalGoal = UserDetails.getString("MonthlyBudget","")?.toFloat()!!
        totalEducation = 0.0f
        totalFood = 0.0f
        totalHealth = 0.0f
        totalOthers = 0.0f
        totalShopping = 0.0f
        totalTransport = 0.0f
        val currency  = UserDetails.getString("Currency","")
        val transactionList = mutableListOf<TransactionData>()
        firebase.collection("Transactions")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("TransactionList")
            .whereEqualTo("year",year)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (data in it.documents){
                        val transaction = data.toObject<TransactionData>()
                        transaction?.let {
                            if (!transactionList.contains(it)){
                                transactionList.add(it)
                            }
                        }
                    }
                }
                if (transactionList.isEmpty()){
                    binding.noTransactionsDoneText.text = "No Transaction done on Year $year"
                    binding.noTransactionsDoneText.visibility = View.VISIBLE
                    binding.textView4.visibility = View.GONE
                    binding.transactionrecyclerview.visibility = View.GONE
                    binding.mainCard.visibility = View.GONE
                }
                else{
                    binding.noTransactionsDoneText.visibility = View.GONE
                    binding.textView4.visibility = View.VISIBLE
                    binding.mainCard.visibility = View.VISIBLE
                    binding.transactionrecyclerview.visibility = View.VISIBLE
                    val transactionAdapter = TransactionAdapter(requireContext(),"AllTransactions",transactionList)
                    binding.transactionrecyclerview.layoutManager = LinearLayoutManager(requireContext())
                    binding.transactionrecyclerview.adapter = transactionAdapter
                }

                for (i in transactionList){
                    totalexpense += i.amount
                    when(i.category){
                        "Food"->{
                            totalFood += i.amount.toFloat()
                        }
                        "Shopping"->{
                            totalShopping += i.amount.toFloat()
                        }
                        "Health"->{
                            totalHealth += i.amount.toFloat()
                        }
                        "Education"->{
                            totalEducation += i.amount.toFloat()
                        }
                        "Transport"->{
                            totalTransport += i.amount.toFloat()
                        }
                        "Others"->{
                            totalOthers += i.amount.toFloat()
                        }
                    }
                }
                binding.budgettv.text = "$currency ${totalGoal.toInt()}"
                binding.expensetv.text = "$currency ${totalexpense.toInt()}"
                binding.datetv.text = "Year: $year"
                if (totalexpense > totalGoal){
                    binding.indicator.setImageResource(R.drawable.ic_negative_transaction)
                    binding.expensetv.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
                }
                else{
                    binding.indicator.setImageResource(R.drawable.ic_positive_amount)
                }
                showPiChart()
            }
            .addOnFailureListener {
                notifyUser("${it.message}")
            }
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
            binding.piechart.addPieSlice(PieModel("Left",totalGoal-(totalexpense.toFloat()), ContextCompat.getColor(requireContext(), R.color.background_deep)))
        }
        binding.piechart.startAnimation()
    }

    private fun setListener(binding: FragmentAllTransactionsBinding){
        binding.january.setOnClickListener(this)
        binding.february.setOnClickListener(this)
        binding.march.setOnClickListener(this)
        binding.april.setOnClickListener(this)
        binding.may.setOnClickListener(this)
        binding.june.setOnClickListener(this)
        binding.july.setOnClickListener(this)
        binding.august.setOnClickListener(this)
        binding.september.setOnClickListener(this)
        binding.october.setOnClickListener(this)
        binding.november.setOnClickListener(this)
        binding.december.setOnClickListener(this)
    }

//    handling click events of the month buttons.
//    it check which button was clicked and perform events accordingly
    override  fun onClick(v:View){
        when(v){
            binding.january->{
                setMonth(v,binding.january)
                monthInt = 1
                showMonthsTransaction()
            }
            binding.february->{
                setMonth(v,binding.february)
                monthInt = 2
                showMonthsTransaction()
            }
            binding.march->{
                setMonth(v,binding.march)
                monthInt = 3
                showMonthsTransaction()
            }
            binding.april->{
                setMonth(v,binding.april)
                monthInt = 4
                showMonthsTransaction()
            }
            binding.may->{
                setMonth(v,binding.may)
                monthInt = 5
                showMonthsTransaction()
            }
            binding.june->{
                setMonth(v,binding.june)
                monthInt = 6
                showMonthsTransaction()
            }
            binding.july->{
                setMonth(v,binding.july)
                monthInt = 7
                showMonthsTransaction()
            }
            binding.august->{
                setMonth(v,binding.august)
                monthInt = 8
                showMonthsTransaction()
            }
            binding.september->{
                setMonth(v,binding.september)
                monthInt = 9
                showMonthsTransaction()
            }
            binding.october->{
                setMonth(v,binding.october)
                monthInt = 10
                showMonthsTransaction()
            }
            binding.november->{
                setMonth(v,binding.november)
                monthInt = 11
                showMonthsTransaction()
            }
            binding.december->{
                setMonth(v,binding.december)
                monthInt = 12
                showMonthsTransaction()
            }


        }
    }
//    this function updates the UI when a button is clicked
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

    private fun notifyUser(msg : String){
        Toast.makeText(requireContext(), "$msg", Toast.LENGTH_SHORT).show()
    }
}