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
import com.example.spendease.navigation.NavigationDrawer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.textfield.TextInputEditText

class GettingInfo : AppCompatActivity() {
    lateinit var letsgobtn : Button
    lateinit var name : TextInputEditText
    lateinit var monthlybudget : TextInputEditText
    lateinit var yearlybudget : TextInputEditText
    lateinit var userDetails: SharedPreferences
    lateinit var currencypicker : Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getting_info)
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
        currencypicker = findViewById(R.id.currencyspinner_id)
        val currency : List<String> = listOf("₹ Indian Rupee","$ Dollar","£ United Kingdom Pound","$ Argentina Peso","ƒ Aruba Guilder","₼ Azerbaijan Manat","Br Belarus Ruble","лв Bulgaria Lev","R$ Brazil Real","៛ Cambodia Riel","¥ China Yuan Renminbi","৳ Bangladeshi taka")
        val currencyadapter = ArrayAdapter(this, R.layout.currency_item,currency)
        currencypicker.adapter = currencyadapter
    }

    private fun goToNextScreen(){
        val intent = Intent(this,NavigationDrawer::class.java)
        startActivity(intent)
    }

    private fun saveUserData(){
        name = findViewById(R.id.entername_id)
        monthlybudget = findViewById(R.id.entermonthlyb_id)
        yearlybudget = findViewById(R.id.enteryearlyb_id)

        val username = name.text.toString()
        val usermonthlybudget = monthlybudget.text.toString().trim()
        val usereyearlybudget = yearlybudget.text.toString().trim()
        val currency = currencypicker.selectedItem.toString()

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