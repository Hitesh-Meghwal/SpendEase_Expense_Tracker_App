package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.spendease.Model.TransactionData
import com.example.spendease.R
import com.example.spendease.databinding.FragmentAddTransactionsBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.log

@Suppress("DEPRECATION")
class AddTransactions : Fragment(),View.OnClickListener {
    private lateinit var binding : FragmentAddTransactionsBinding
    private var category = ""
    lateinit var bottomnav : BottomNavigationView
    lateinit var toolbar: MaterialToolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var userDetails : SharedPreferences
    var day = 0
    var month = 0
    var year = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentAddTransactionsBinding.inflate(inflater,container,false)
        bottomnav = requireActivity().findViewById(R.id.bottomnavigation_id)
        binding.newtranstoolbarId.navigationIcon = ContextCompat.getDrawable(requireContext(),R.drawable.baseline_arrow_back_24)
        binding.newtranstoolbarId.setNavigationOnClickListener {
            val action = AddTransactionsDirections.actionAddTransactionsToDashboard()
            findNavController().navigate(action)
        }
        setListener(binding)
        datePicker(binding)
        userDetails = requireActivity().getSharedPreferences("UserDetails",AppCompatActivity.MODE_PRIVATE)
//        if(transactions.from){
//            setDatas()
//            binding.addtransaction.text = "Save Transaction"
//            binding.newtranstoolbarId.title = "Edit Transaction"
//        }
        binding.addtransaction.setOnClickListener {
            addTransaction()
        }
        return binding.root
    }


    private fun setListener(binding: FragmentAddTransactionsBinding){
        binding.foodbtn.setOnClickListener(this)
        binding.transportbtn.setOnClickListener(this)
        binding.healthbtn.setOnClickListener(this)
        binding.otherbtn.setOnClickListener(this)
        binding.shoppingbtn.setOnClickListener(this)
        binding.educationbtn.setOnClickListener(this)
    }

    private fun setDatas(){
        val transactiondata = TransactionData()
        binding.title.setText(transactiondata.title)
        binding.date.setText(transactiondata.date)
        binding.amount.setText(transactiondata.amount.toString())
        binding.note.setText(transactiondata.note)
        category = transactiondata.category
        when(category){
            "Food"->{
                setCategory(binding.foodbtn,binding.foodbtn)
            }
            "Shopping"->{
                setCategory(binding.shoppingbtn,binding.shoppingbtn)
            }
            "Transport"->{
                setCategory(binding.transportbtn,binding.transportbtn)
            }
            "Health"->{
                setCategory(binding.healthbtn,binding.healthbtn)
            }
            "Other"->{
                setCategory(binding.otherbtn,binding.otherbtn)
            }
            "Education"->{
                setCategory(binding.educationbtn,binding.educationbtn)
            }
        }
    }
    private fun addTransaction(){
        val title = binding.title.text.toString()
        val date = binding.date.text.toString()
        val amount = binding.amount.text.toString().trim()
        val note = binding.note.text.toString()
        if (title.isEmpty() || date.isEmpty() || amount.isEmpty() || note.isEmpty() || category == ""){
            notifyUser("Enter all required details")
        }
        else{
            val firestore = FirebaseFirestore.getInstance()
            val newTransactions = TransactionData("Expense",category,title,amount.toDouble(),date,day,month,year,note)
            firestore.collection("Transactions")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("TransactionList")
                .add(newTransactions)
                .addOnSuccessListener {
                    notifyUser("Transaction Added Successfully")
                    val action = AddTransactionsDirections.actionAddTransactionsToDashboard()
                    findNavController().navigate(action)
                }
                .addOnFailureListener { e->
                    notifyUser("Something went wrong"+e.message)
                }

        }
    }

    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    private fun datePicker(binding: FragmentAddTransactionsBinding){
        val cal = Calendar.getInstance()
        binding.date.setText(SimpleDateFormat("dd MMMM YYYY").format(System.currentTimeMillis()))
        day = SimpleDateFormat("dd").format(System.currentTimeMillis()).toInt()
        month = SimpleDateFormat("MM").format(System.currentTimeMillis()).toInt()
        year = SimpleDateFormat("YYYY").format(System.currentTimeMillis()).toInt()
        val dateSetListener = OnDateSetListener{_,Year,monthOfYear,dayOfMonth->
            cal.set(Calendar.YEAR,Year)
            cal.set(Calendar.MONTH,monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            var myFormat = "dd MMMM YYYY"
            var simpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
            binding.date.setText(simpleDateFormat.format(cal.time))
            myFormat = "dd"
            simpleDateFormat = SimpleDateFormat(myFormat,Locale.US)
            day = simpleDateFormat.format(cal.time).toInt()

            myFormat = "MM"
            simpleDateFormat = SimpleDateFormat(myFormat,Locale.US)
            month = simpleDateFormat.format(cal.time).toInt()

            myFormat="YYYY"
            simpleDateFormat = SimpleDateFormat(myFormat,Locale.US)
            year = simpleDateFormat.format(cal.time).toInt()
        }
        binding.date.setOnClickListener {
            DatePickerDialog(requireContext(),dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

    }
    override fun onClick(v:View){
        when(v){
            binding.foodbtn ->{
                setCategory(v,binding.foodbtn)
            }
            binding.shoppingbtn->{
                setCategory(v,binding.shoppingbtn)
            }
            binding.educationbtn->{
                setCategory(v,binding.educationbtn)
            }
            binding.transportbtn->{
                setCategory(v,binding.transportbtn)
            }
            binding.otherbtn->{
                setCategory(v,binding.otherbtn)
            }
            binding.healthbtn->{
                setCategory(v,binding.healthbtn)
            }

        }
    }
    @SuppressLint("PrivateResource")
    private fun setCategory(v: View, button:MaterialButton){
        category = button.text.toString()
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), com.google.android.material.R.color.mtrl_btn_text_btn_bg_color_selector))
        button.setIconTintResource(R.color.purple_200)
        button.setStrokeColorResource(R.color.purple_200)
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.purple_200))
        when(v){
            binding.foodbtn->{
                removeBackground(binding.shoppingbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.healthbtn)
            }
            binding.shoppingbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.healthbtn)
            }
            binding.transportbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.shoppingbtn)
                removeBackground(binding.healthbtn)
            }
            binding.otherbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.shoppingbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.healthbtn)
            }
            binding.educationbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.shoppingbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.healthbtn)
            }
            binding.healthbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.shoppingbtn)
            }

        }
    }
    private fun removeBackground(button: MaterialButton){
        button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.transparent))
        button.setIconTintResource(R.color.textSecondary)
        button.setStrokeColorResource(R.color.textSecondary)
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.textSecondary))
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        bottomnav.visibility = View.GONE
    }

    private fun notifyUser(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}