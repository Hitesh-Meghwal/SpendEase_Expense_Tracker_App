package com.example.spendease.userAuthentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call.Details
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.example.spendease.R
import com.example.spendease.databinding.ActivityGettingInfoBinding
import com.example.spendease.navigation.NavigationDrawer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.textfield.TextInputEditText

class GettingInfo : AppCompatActivity() {
    private lateinit var binding : ActivityGettingInfoBinding
    lateinit var letsgobtn : Button
    lateinit var name : TextInputEditText
    lateinit var userDetails: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGettingInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setData()
    }

    private fun setData(){
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        letsgobtn = findViewById(R.id.letsgo_id)
        currencyselector()
        letsgobtn.setOnClickListener {
            saveUserData()
        }
    }

    private fun currencyselector(){
        val currency : List<String> = listOf("₹ Indian Rupee","$ Dollar","£ United Kingdom Pound","$ Argentina Peso","ƒ Aruba Guilder","₼ Azerbaijan Manat","Br Belarus Ruble","лв Bulgaria Lev","R$ Brazil Real","៛ Cambodia Riel","¥ China Yuan Renminbi","৳ Bangladeshi taka")
        val currencyadapter = ArrayAdapter(this, R.layout.currency_item,currency)
        binding.currencyspinnerId.adapter = currencyadapter
    }

    private fun goToNextScreen(){
        val intent = Intent(this,NavigationDrawer::class.java)
        startActivity(intent)
    }

    private fun saveUserData(){

        val username = binding.enternameId.text.toString()
        val usermonthlybudget =binding.entermonthlybId.text.toString().trim()
        val usereyearlybudget = binding.enteryearlybId.text.toString().trim()
        val currency = binding.currencyspinnerId.selectedItem.toString()

        if (username.isEmpty() || usermonthlybudget.isEmpty() || usereyearlybudget.isEmpty()){
            Toast.makeText(this, "Enter all details to continue...", Toast.LENGTH_SHORT).show()
        }
        else{
            userDetails = this.getSharedPreferences("UserDetails", MODE_PRIVATE)
            val editor = userDetails.edit()
            editor.putBoolean("isFirstTime",false)
            editor.putString("Name",username)
            editor.putString("MonthlyBudget",usermonthlybudget)
            editor.putString("YearlyBudget",usereyearlybudget)
            editor.putString("Currency_name",currency.trim())
            editor.putString("Currency",currency.split(" ")[0].trim())
            editor.apply()
            goToNextScreen()

        }
    }
}