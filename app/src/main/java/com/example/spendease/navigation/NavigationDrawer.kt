package com.example.spendease.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.spendease.Model.TransactionData
import com.example.spendease.R
import com.example.spendease.databinding.ActivityNavigationDrawerBinding
import com.example.spendease.userAuthentication.Signin
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.toObject
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.exp

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
                    val firebase = FirebaseFirestore.getInstance()
                        .collection("Transactions")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .collection("TransactionList")
                        .get()
                        .addOnSuccessListener {
                            val expenseData = mutableListOf<TransactionData>()
                            if (!it.isEmpty){
                                for (data in it.documents){
                                    val transaction = data.toObject(TransactionData::class.java)
                                    transaction?.let {
                                        if (!expenseData.contains(it)){
                                            expenseData.add(it)
                                        }
                                    }
                                }
                            }
                            generatePdf(expenseData)
                            Toast.makeText(this, "Generating Monthly PDF", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{e->
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
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

    @SuppressLint("SimpleDateFormat")
    private fun generatePdf(expenseData : List<TransactionData>){
        try{
            val document = Document()
            // Get the current date and time for the PDF file name
            val pdfFileName = "ExpenseReport_${SimpleDateFormat("yyyMMdd").format(Date())}.pdf"
//            val pdfFile = File(Environment.getExternalStorageDirectory(),pdfFileName
            val storageDir = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val pdfFile = File(storageDir,pdfFileName)
            val pdfWriter = PdfWriter.getInstance(document,FileOutputStream(pdfFile))

            document.open()

            for(expense in expenseData){
                val expensInfo = "Title: ${expense.title}\nAmount: ${expense.amount}\nDate: ${expense.date}\nNote: ${expense.note}"
                document.add(Paragraph(expensInfo))
            }

            document.close()
            Log.d("PDFGenerator", "PDF File saved as: ${pdfFile.absolutePath}")

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(pdfFile),"application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            this.startActivity(intent)
        }
        catch (e:Exception){
            Log.d("Pdf Failed to Generate","${e.message}")
        }
    }
}