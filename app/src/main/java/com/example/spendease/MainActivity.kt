package com.example.spendease

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.spendease.navigation.NavigationDrawer
import com.example.spendease.onBoarding.ViewPagerActivity
import com.example.spendease.userAuthentication.Signup
import com.google.android.gms.auth.api.signin.GoogleSignIn
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            // if user is already login then he/she redirect to next activity if user is login with GoogleAccount
            val account = GoogleSignIn.getLastSignedInAccount(this)
            // if user is already login then he/she redirect to next activity if user is login with email and password
            val userDetails = getSharedPreferences("UserDetails", MODE_PRIVATE)
            val check = userDetails.getBoolean("isFirstTime",false)
            val checkOnBoarding = userDetails.getBoolean("onboarding_completed",false)

            if(shouldShowOnboarding()){
                val onBoarding = Intent(this,ViewPagerActivity::class.java)
                startActivity(onBoarding)
                finish()
            }
            else {
                if (check) {
                    val i = Intent(this, NavigationDrawer::class.java)
                    startActivity(i)
                    finish()
                } else {
                    val i = Intent(this, Signup::class.java)
                    startActivity(i)
                    finish()
                }
            }
            finish()
        },3000)

    }

    private fun shouldShowOnboarding(): Boolean {
        val preferences = getSharedPreferences("OnboardingPrefs", MODE_PRIVATE)
        // Check if a flag indicating onboarding completion is set
        return !preferences.getBoolean("onboarding_completed", false)
    }
}
