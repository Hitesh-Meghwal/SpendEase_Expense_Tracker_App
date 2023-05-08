package com.example.spendease.userAuthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.spendease.R
import com.example.spendease.navigation.NavigationDrawer

class GettingInfo : AppCompatActivity() {
    lateinit var letsgobtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getting_info)

        letsgobtn = findViewById(R.id.letsgo_id)
        letsgobtn.setOnClickListener {
            val mainScreenintent = Intent(this,NavigationDrawer::class.java)
            startActivity(mainScreenintent)
        }

    }
}