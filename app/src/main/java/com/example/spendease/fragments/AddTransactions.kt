package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.spendease.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class AddTransactions : Fragment(),View.OnClickListener {
    val transactions by navArgs<AddTransactionsArgs>()
    private var category = ""
    lateinit var food : MaterialButton
    lateinit var shopping : MaterialButton
    lateinit var transport : MaterialButton
    lateinit var others : MaterialButton
    lateinit var education : MaterialButton
    lateinit var health : MaterialButton
    lateinit var bottomnav : BottomNavigationView
    lateinit var toolbar: MaterialToolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var userDetails : SharedPreferences
    var day = 0
    var month = 0
    var year = 0
//    private val viewModal :TransactionViewModal
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_transactions, container, false)
        bottomnav = requireActivity().findViewById(R.id.bottomnavigation_id)
        toolbar = view.findViewById(R.id.newtranstoolbar_id)
        food = view.findViewById(R.id.foodbtn)
        shopping = view.findViewById(R.id.shoppingbtn)
        education = view.findViewById(R.id.educationbtn)
        others = view.findViewById(R.id.otherbtn)
        transport = view.findViewById(R.id.transportbtn)
        health = view.findViewById(R.id.healthbtn)

        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(),R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            val action = AddTransactionsDirections.actionAddTransactionsToDashboard()
            findNavController().navigate(action)
        }
        userDetails = requireActivity().getSharedPreferences("UserDetails",AppCompatActivity.MODE_PRIVATE)
        if (transactions.from){

        }
        return view
    }

    private fun setListener(rootView: View){
        rootView.findViewById<MaterialButton>(R.id.foodbtn).setOnClickListener { this }
        rootView.findViewById<MaterialButton>(R.id.shoppingbtn).setOnClickListener { this }
        rootView.findViewById<MaterialButton>(R.id.educationbtn).setOnClickListener { this }
        rootView.findViewById<MaterialButton>(R.id.otherbtn).setOnClickListener { this }
        rootView.findViewById<MaterialButton>(R.id.transportbtn).setOnClickListener { this }
        rootView.findViewById<MaterialButton>(R.id.healthbtn).setOnClickListener { this }
    }

    private fun setDatas(rootView: View){
        rootView.findViewById<EditText>(R.id.title).setText(transactions.data.title)
        rootView.findViewById<EditText>(R.id.amount).setText(transactions.data.amount.toString())
        rootView.findViewById<EditText>(R.id.date).setText(transactions.data.date)
        rootView.findViewById<EditText>(R.id.note).setText(transactions.data.note)
        category = transactions.data.category
        when(category){
            "Food"->{

            }
        }
    }

    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    private fun datePicker(rootView: View){
        val cal = Calendar.getInstance()
        rootView.findViewById<EditText>(R.id.date).setText(SimpleDateFormat("dd MMMM YYYY").format(System.currentTimeMillis()))
        day = SimpleDateFormat("dd").format(System.currentTimeMillis()).toInt()
        month = SimpleDateFormat("MM").format(System.currentTimeMillis()).toInt()
        year = SimpleDateFormat("YYYY").format(System.currentTimeMillis()).toInt()
        val dateSetListener = OnDateSetListener{_,Year,monthOfYear,dayOfMonth->
            cal.set(Calendar.YEAR,Year)
            cal.set(Calendar.MONTH,monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            var myFormat = "dd MMMM YYYY"
            var simpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
            rootView.findViewById<EditText>(R.id.date).setText(simpleDateFormat.format(cal.time))

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
        rootView.findViewById<EditText>(R.id.date).setOnClickListener {
            DatePickerDialog(requireContext(),dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

    }
    override fun onClick(v:View?){
        when(v){
            food ->{
                setCategory(v,food)
            }
            shopping->{
                setCategory(v,shopping)
            }
            education->{
                setCategory(v,education)
            }
            transport->{
                setCategory(v,transport)
            }
            others->{
                setCategory(v,others)
            }
            health->{
                setCategory(v,health)
            }

        }
    }
    @SuppressLint("PrivateResource")
    private fun setCategory(v: View, button:MaterialButton){
        category = button.text.toString()
        button.setBackgroundColor(ContextCompat.getColor(requireContext(),
            com.google.android.material.R.color.mtrl_btn_text_btn_bg_color_selector))
        button.setIconTintResource(R.color.purple_200)
        button.setStrokeColorResource(R.color.purple_200)
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.purple_200))
        when(v){
            food->{
                removeBackground(shopping)
                removeBackground(education)
                removeBackground(others)
                removeBackground(transport)
                removeBackground(health)
            }
            shopping->{
                removeBackground(food)
                removeBackground(education)
                removeBackground(others)
                removeBackground(transport)
                removeBackground(health)
            }
            transport->{
                removeBackground(food)
                removeBackground(shopping)
                removeBackground(others)
                removeBackground(education)
                removeBackground(health)
            }
            others->{
                removeBackground(food)
                removeBackground(shopping)
                removeBackground(education)
                removeBackground(transport)
                removeBackground(health)
            }
            education->{
                removeBackground(food)
                removeBackground(shopping)
                removeBackground(others)
                removeBackground(transport)
                removeBackground(health)
            }
            health->{
                removeBackground(food)
                removeBackground(shopping)
                removeBackground(others)
                removeBackground(education)
                removeBackground(transport)
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



}