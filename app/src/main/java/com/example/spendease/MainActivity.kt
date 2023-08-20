package com.example.spendease

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.spendease.navigation.NavigationDrawer
import com.example.spendease.userAuthentication.GettingInfo
import com.example.spendease.userAuthentication.Signin
import com.example.spendease.userAuthentication.Signup
import com.google.android.gms.auth.api.signin.GoogleSignIn

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.button_id)
        btn.setOnClickListener {
            checkingUserExistence()
        }
    }

    private fun checkingUserExistence(){
        // if user is already login then he/she redirect to next activity if user is login with GoogleAccount
        val account = GoogleSignIn.getLastSignedInAccount(this)
        // if user is already login then he/she redirect to next activity if user is login with email and password
        val userDetails = getSharedPreferences("UserDetails", MODE_PRIVATE)
        val check = userDetails.getBoolean("isFirstTime",false)
        if(check) {
            val i = Intent(this,NavigationDrawer::class.java)
            startActivity(i)
        }
        else{
            val i = Intent(this,Signup::class.java)
            startActivity(i)
        }
    }
}