package com.example.spendease.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.spendease.R
import com.example.spendease.userAuthentication.Login
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class NavigationDrawer : AppCompatActivity(){
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: MaterialToolbar
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)

        drawerLayout =findViewById(R.id.drawerlayout)
        navigationView = findViewById(R.id.navigation_drawer)
        toolbar = findViewById(R.id.toolbar_id)
        setSupportActionBar(toolbar)

        val bottomnav = findViewById<BottomNavigationView>(R.id.bottomnavigation_id)


        val actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_navigation_drawer,R.string.close_navigation_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navController = findNavController(R.id.fragment)
        bottomnav.setupWithNavController(navController)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_id->{
                val userDetails = getSharedPreferences("UserDetails", MODE_PRIVATE)
                val userEmail = userDetails.getString("email","")
                val editor = userDetails.edit()
//                editor.putBoolean("isFirstTime",false)
//                editor.remove(userEmail + "Name")
//                editor.remove(userEmail + "MonthlyBudget")
//                editor.remove(userEmail + "YearlyBudget")
//                editor.remove(userEmail + "Currency_name")
//                editor.remove(userEmail + "Currency")
                editor.apply()
//                FirebaseAuth.getInstance().signOut()
                val i = Intent(this,Login::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }



}