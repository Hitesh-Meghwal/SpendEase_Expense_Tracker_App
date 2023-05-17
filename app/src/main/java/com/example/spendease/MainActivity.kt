package com.example.spendease

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.spendease.navigation.NavigationDrawer
import com.example.spendease.userAuthentication.Login

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.button_id)
        btn.setOnClickListener {
            checkingUserExistence()
        }

    }

    private fun checkingUserExistence(){
        val userDetails = getSharedPreferences("UserDetails", MODE_PRIVATE)
        val check = userDetails.getBoolean("isFirstTime",false)
        if(check) {
            val i = Intent(this,NavigationDrawer::class.java)
            startActivity(i)
        }
        else{
            val i = Intent(this,Login::class.java)
            startActivity(i)
        }
    }
}