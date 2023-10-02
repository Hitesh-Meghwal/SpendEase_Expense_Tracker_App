package com.example.spendease.navigation

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.exp

class NavigationDrawer : AppCompatActivity(){
    private lateinit var binding: ActivityNavigationDrawerBinding
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: MaterialToolbar
    lateinit var firebase : FirebaseFirestore
    lateinit var userDetails : SharedPreferences
    private var selectedMonth:Int = 0
    private var selectedYear:Int = 0
    private val CHANNEL_ID = "PDF_GENERATION_CHANNEL"
    private val NOTIFICATION_ID = 100

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

        firebase = FirebaseFirestore.getInstance()
        userDetails = getSharedPreferences("UserDetails", MODE_PRIVATE)
        val imgid = userDetails.getString("UserImageid","")
        val navHeaderImg = headerView.findViewById<ShapeableImageView>(R.id.userImage)
        if (!imgid.isNullOrEmpty() && navHeaderImg != null){
            val imgurl = userDetails.getString("UserImage","")

            Picasso.get().load(imgurl).into(navHeaderImg)
        }

        else{

        }
        navigationItemEvent()
        createNotificationChannel()
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
    @SuppressLint("SimpleDateFormat", "CutPasteId")
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view,Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.monthlypdf -> {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.dialog_pdf)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.show()
                    val formatyear = SimpleDateFormat("YYYY")
                    val currentYear = formatyear.format(Calendar.getInstance().time).toInt()
                    val monthSpinner = dialog.findViewById<Spinner>(R.id.spinner)
                    val generatebtn = dialog.findViewById<MaterialButton>(R.id.generatebtn)
                    val monthMap = mapOf(
                        "JAN" to 1, "FEB" to 2,
                        "MAR" to 3, "APR" to 4,
                        "MAY" to 5, "JUN" to 6,
                        "JUL" to 7, "AUG" to 8,
                        "SEP" to 9, "OCT" to 10,
                        "NOV" to 11, "DEC" to 12
                    )
                    generatebtn.setOnClickListener {
                        val selectedMonthName = monthSpinner.selectedItem
                        selectedMonth = monthMap[selectedMonthName] ?: 1
                        // Handle generating monthly PDF
                        firebase.collection("Transactions")
                            .document(FirebaseAuth.getInstance().uid.toString())
                            .collection("TransactionList")
                            .whereEqualTo("month",selectedMonth)
                            .whereEqualTo("year",currentYear)
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
                                generatePdf(expenseData,selectedMonthName.toString(),"monthly")
                                Toast.makeText(this, "Generating Monthly PDF", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .addOnFailureListener{e->
                                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    true
                }
                R.id.yearlypdf -> {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.dialog_pdf)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.show()
                    // Handle generating yearly PDF
                    val formatyear = SimpleDateFormat("YYYY")
                    val currentYear = formatyear.format(Calendar.getInstance().time).toInt()
                    val yearSpinner = dialog.findViewById<Spinner>(R.id.spinner)
                    val yearlist = mutableListOf<Int>()
                    for(i in currentYear downTo 2022){
                        yearlist += i
                    }
                    val yearAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,yearlist)
                    yearSpinner.adapter = yearAdapter
                    val generatebtn = dialog.findViewById<MaterialButton>(R.id.generatebtn)

                    generatebtn.setOnClickListener {
                        selectedYear = yearSpinner.selectedItem.toString().toInt()
                        firebase.collection("Transactions")
                            .document(FirebaseAuth.getInstance().uid.toString())
                            .collection("TransactionList")
                            .whereEqualTo("year",selectedYear )
                            .get()
                            .addOnSuccessListener {
                                val expenseData = mutableListOf<TransactionData>()
                                if (!it.isEmpty) {
                                    for (data in it.documents) {
                                        val transaction = data.toObject(TransactionData::class.java)
                                        transaction?.let {
                                            if (!expenseData.contains(it)) {
                                                expenseData.add(it)
                                            }
                                        }
                                    }
                                }
                                generatePdf(expenseData, selectedYear.toString(),"yearly")
                                Toast.makeText(this, "Generating Yearly PDF", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SimpleDateFormat")
    private fun generatePdf(expenseData : List<TransactionData>,selected:String,selectedPeriod:String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val document = Document()
                // Get the current date and time for the PDF file name
                val pdfFileName = "ExpenseReport_${selected}_${SimpleDateFormat("dd").format(Date())}.pdf"
                val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val pdfFile = File(storageDir, pdfFileName)
                val pdfWriter = PdfWriter.getInstance(document, FileOutputStream(pdfFile))

                document.open()
                document.addAuthor("Hitesh Meghwal")
                document.addTitle("Transaction of $selected")

                val table = PdfPTable(5) //table with 5 columns
                table.widthPercentage = 100f
                val headers = arrayOf("TITLE","CATEGORY","AMOUNT","DATE","NOTE")

                for (header in headers){
//                    it represents the header text that you want to display in the table cell.
                    val cell = PdfPCell(Phrase(header))
                    cell.backgroundColor = BaseColor.LIGHT_GRAY
                    table.addCell(cell)
                }

                for (expense in expenseData) {
                    if (selectedPeriod == "monthly" && expense.month == selectedMonth) {
                        table.addCell(expense.title)
                        table.addCell(expense.category)
                        table.addCell(expense.amount.toString())
                        table.addCell(expense.date)
                        table.addCell(expense.note)
                    }
                    else if (selectedPeriod =="yearly" && expense.year == selectedYear){
                        table.addCell(expense.title)
                        table.addCell(expense.category)
                        table.addCell(expense.amount.toString())
                        table.addCell(expense.date)
                        table.addCell(expense.note)
                    }
                }

                val totalexpense = calculateTotalExpense(expenseData,selectedPeriod)
                table.addCell("Total")
                table.addCell("")
                table.addCell(totalexpense)
                table.addCell("")
                table.addCell("")

                document.add(table)

                document.close()
                sendPdfNotification(pdfFile)
                Log.d("PDFGenerator", "PDF File saved as: ${pdfFile.absolutePath}")

            } catch (e: Exception) {
                Log.d("Pdf Failed to Generate", "${e.message}")
            }
        }
    }

    private fun calculateTotalExpense(expenseData: List<TransactionData>, period:String):String{
        var totalExpense = 0.0
        for(expense in expenseData){
            // Add the expense amount to the total if it matches the selected period
            if (period == "monthly" && expense.month == selectedMonth){
                totalExpense += expense.amount
            }
            else if (period == "yearly" && expense.year == selectedYear){
                totalExpense += expense.amount
            }
        }
        return String.format("%.2f", totalExpense)
    }

    @SuppressLint("MissingPermission")
    private fun sendPdfNotification(pdfFile: File){

        val openIntent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(this,"${this.packageName}.provider",pdfFile)
        openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        openIntent.setDataAndType(uri,"application/pdf")
//
        val pendingIntent = PendingIntent.getActivity(this,0,openIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_picture_as_pdf_24)
            .setContentTitle("PDF Generated")
            .setContentText("Your PDF has been generated successfully.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)  // Automatically removes the notification when tapped
            .build()

//        send notification
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID,notification)

    }

//    creating notification channel
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "PDF Notifications"
            val destext = "PDF generation notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = destext
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}