package com.example.spendease.UserAuthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.spendease.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class Login : AppCompatActivity() {
    lateinit var signuptv : TextView
    lateinit var loginbtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signuptv = findViewById(R.id.signuptv_id)
        loginbtn = findViewById(R.id.Loginbtn_id)

        signuptv.setOnClickListener {
            val signupintent = Intent(this,Signup::class.java)
            startActivity(signupintent)
        }
        loginbtn.setOnClickListener {
            val gettingInfointent = Intent(this,GettingInfo::class.java)
            startActivity(gettingInfointent)
        }



    }


}