package com.example.spendease.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.spendease.R
import com.example.spendease.databinding.ActivityNavigationDrawerBinding
import com.example.spendease.fragments.Dashboard
import com.example.spendease.fragments.DashboardDirections
import com.example.spendease.userAuthentication.Signin
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class NavigationDrawer : AppCompatActivity(){
    private lateinit var binding: ActivityNavigationDrawerBinding
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout =findViewById(R.id.drawerlayout)
        navigationView = findViewById(R.id.navigation_drawer)

        val headerView = navigationView.getHeaderView(0)
        toolbar = findViewById(R.id.toolbar_id)
        setSupportActionBar(toolbar)

        val bottomnav = findViewById<BottomNavigationView>(R.id.bottomnavigation_id)

        val actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_navigation_drawer,R.string.close_navigation_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        bottomnav.setupWithNavController(navController)
        navigationView.setupWithNavController(navController)

        val userDetails = getSharedPreferences("UserDetails", MODE_PRIVATE)
        val imgid = userDetails.getString("UserImageid","")
        val navHeaderImg = headerView.findViewById<ShapeableImageView>(R.id.userImage)
        if (!imgid.isNullOrEmpty() && navHeaderImg != null){
            val imgurl = userDetails.getString("UserImage","")

            Picasso.get().load(imgurl).into(navHeaderImg)
        }

        else{

        }
        navigationItemEvent()
    }

    private fun navigationItemEvent() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            when (id) {
                R.id.nav_home -> {
                    val navController = findNavController(R.id.fragmentContainerView)
                    navController.navigate(R.id.dashboard)
//                    val i = Intent(this, Dashboard::class.java)
//                    startActivity(i)
                    closeDrawer()
                    true
                }

                R.id.currencyconvertor_id -> {
                    val navController = findNavController(R.id.fragmentContainerView)
                    navController.navigate(R.id.currencyconvertor_id)
                    closeDrawer()
//                    val action = DashboardDirections.actionDashboardToCurrencyConverter()
//                    Navigation.findNavController(binding.root).navigate(action)
                    true
                }

                else -> {
                    false
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.reports_id ->{
                showPopupMenu(toolbar)
            }
            R.id.logout_id->{
                val userDetails = getSharedPreferences("UserDetails", MODE_PRIVATE)
                val editor = userDetails.edit()
                editor.putBoolean("isFirstTime",false)
                editor.apply()
                FirebaseAuth.getInstance().signOut()
                val i = Intent(this,Signin::class.java)
                startActivity(i)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun closeDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START)
    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view,Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.monthlypdf -> {
                    // Handle generating monthly PDF
                    Toast.makeText(this, "Generating Monthly PDF", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.yearlypdf -> {
                    // Handle generating yearly PDF
                    Toast.makeText(this, "Generating Yearly PDF", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }




}