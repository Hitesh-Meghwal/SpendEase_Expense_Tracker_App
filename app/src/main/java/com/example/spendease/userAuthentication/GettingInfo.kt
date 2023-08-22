package com.example.spendease.userAuthentication

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.spendease.R
import com.example.spendease.databinding.ActivityGettingInfoBinding
import com.example.spendease.navigation.NavigationDrawer
import com.google.android.material.textfield.TextInputEditText

class GettingInfo : AppCompatActivity() {
    private lateinit var binding : ActivityGettingInfoBinding
    lateinit var name : TextInputEditText
    lateinit var userDetails: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGettingInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currencySpinner()
        setData()
    }
    private fun currencySpinner(){
        val currency = resources.getStringArray(R.array.Currency)
        val currencyadapter = ArrayAdapter(this, R.layout.currency_item,currency)
        binding.currencyspinnerId.adapter = currencyadapter
    }
    private fun setData(){
        userDetails = this.getSharedPreferences("UserDetails", MODE_PRIVATE)
        binding.letsgoId.setOnClickListener {
            saveUserData()
        }
    }
    private fun saveUserData(){

        val username = binding.enternameId.text.toString()
        val usermonthlybudget =binding.entermonthlybId.text.toString().trim()
        val usereyearlybudget = binding.enteryearlybId.text.toString().trim()
        val currencyspinner = binding.currencyspinnerId.selectedItem.toString()

        if (username.isEmpty() || usermonthlybudget.isEmpty() || usereyearlybudget.isEmpty() || currencyspinner.isEmpty()){
            Toast.makeText(this, "Enter all details to continue...", Toast.LENGTH_SHORT).show()
        }
        else{
            val editor = userDetails.edit()
            editor.putBoolean("isFirstTime",true)
            editor.putString("Name",username)
            editor.putString("MonthlyBudget",usermonthlybudget)
            editor.putString("YearlyBudget",usereyearlybudget)
            editor.putString("Currency_name",currencyspinner.trim())
            editor.putString("Currency",currencyspinner.split(" ")[0].trim())
            editor.apply()
            goToNextScreen()
        }
    }

    private fun goToNextScreen() {
        val intent = Intent(this, NavigationDrawer::class.java)
        startActivity(intent)
        finish()
    }
}