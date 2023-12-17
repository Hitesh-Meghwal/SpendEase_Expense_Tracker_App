package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendease.Adapter.TransactionAdapter
import com.example.spendease.Model.TransactionData
import com.example.spendease.R
import com.example.spendease.ReminderReceiver
import com.example.spendease.SwipetoDelete.SwipeToDelete
import com.example.spendease.Widget.ExpenseWidget
import com.example.spendease.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.eazegraph.lib.models.PieModel
import java.text.SimpleDateFormat
import java.util.Calendar


@Suppress("DEPRECATION")
class Dashboard : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private var totalExpense = 0.0
    private var totalGoal = 5000.0f
    private var totalFood = 0.0f
    private var totalShopping = 0.0f
    private var totalTransport = 0.0f
    private var totalHealth = 0.0f
    private var totalEducation = 0.0f
    private var totalOthers = 0.0f
    private val CHANNEL_ID = "Expense_Has_Been_Exceed"
    private val NOTIFICATION_ID = 101
    lateinit var userDetails: SharedPreferences
    private val firestore = FirebaseFirestore.getInstance()
    lateinit var adapter: TransactionAdapter
    val transactionlist = mutableListOf<TransactionData>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater,container,false)
//        Switch to AddTransaction Fragment
        val args = DashboardDirections.actionDashboardToAddTransactions(TransactionData(null,"","","",0.0,"",0,0,0,""),false)
        binding.addnewtransactions.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(args)
        }

        adapter = TransactionAdapter(requireContext(), "Dashboard", transactionlist)
        val swipeToDelete = SwipeToDelete(firestore,adapter ,binding.root, transactionlist) { getdata() }
        val itemTouchHelper = ItemTouchHelper(swipeToDelete)
        itemTouchHelper.attachToRecyclerView(binding.dashboardrecyclerview)
        createNotificationChannel()
        if(!isShimmerAlreadyShown()) {
            val shimmerFrame = binding.shimmerView
            val mainLayout = binding.dashboardrecyclerview
            mainLayout.visibility = View.GONE
            shimmerFrame.startShimmer()
            Handler().postDelayed({
                shimmerFrame.stopShimmer()
                shimmerFrame.visibility = View.GONE
                mainLayout.visibility = View.VISIBLE
                getdata()
                markShimmerAsShown()  //Mark the shimmer as shown
            }, 2000)
        }
        else{
            binding.shimmerView.visibility = View.GONE
            binding.dashboardrecyclerview.visibility = View.VISIBLE
            getdata()
        }

        val intent = Intent(context, ExpenseWidget::class.java)
        intent.action = "com.example.spendease.UPDATE_WIDGET"
        context?.sendBroadcast(intent)
//        scheduleAfternoonAlarm()
//        scheduleEveningAlarm()
        return binding.root

    }
    private fun isShimmerAlreadyShown():Boolean{
        val prefrences = requireActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE)
        return prefrences.getBoolean("shimmerShown",false)
    }
    private fun markShimmerAsShown(){
        val preferences = requireActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("shimmerShown",true)
        editor.apply()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n", "MissingPermission")
    private fun getdata(){

        userDetails = requireActivity().getSharedPreferences("UserDetails",MODE_PRIVATE)
        val editor = userDetails.edit()
        val getname = userDetails.getString("Name","")
        val getMonthlyBudget = userDetails.getString("MonthlyBudget","")
        val getCurrency = userDetails.getString("Currency","")

        val formatmonth = SimpleDateFormat("MM")
        val currentMonth = formatmonth.format(Calendar.getInstance().time)
        val formatyear = SimpleDateFormat("YYYY")
        val currentYear = formatyear.format(Calendar.getInstance().time)
        val format = SimpleDateFormat("MMMM")
        binding.datetv.text = "${format.format(Calendar.getInstance().time)} $currentYear"
        editor.putString("date","${format.format(Calendar.getInstance().time)} $currentYear")
        editor.apply()
//        Greeting to user
        val calendar = Calendar.getInstance()
        val greeting = when(calendar.get(Calendar.HOUR_OF_DAY)){
            in 0..4 -> "Good Night"
            in 5..11 -> "Good Morning"
            in 12..16 ->"Good Afternoon"
            else -> "Good evening"
        }
        binding.nametv.text = "$greeting $getname !!"

        totalExpense = 0.0
        totalGoal = getMonthlyBudget?.toFloat()!!
        totalFood = 0.0f
        totalHealth = 0.0f
        totalEducation = 0.0f
        totalOthers = 0.0f
        totalShopping = 0.0f
        totalTransport = 0.0f

        firestore.collection("Transactions")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("TransactionList")
            .whereEqualTo("month",currentMonth.toInt())
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val transaction = data.toObject(TransactionData::class.java)
                        transaction?.let {
//                            checks if the current TransactionData object (it) is already present in the transactionlist.
//                            If the transactionlist doesn't contain the current TransactionData object,
//                            the object is added to the transactionlist using transactionlist.add(it).
                            if (!transactionlist.contains(it)) {
                                transactionlist.add(it)
                            }
                        }
                    }
                }

                if (transactionlist.isEmpty()) {
                    binding.noTransactionsDoneText.text =
                        "Add Your First Transaction of ${format.format(Calendar.getInstance().time)} $currentYear \n Click On + to add Transaction"
                    binding.noTransactionsDoneText.visibility = View.VISIBLE
                    binding.recenttransaction.visibility = View.GONE
                } else {
                    binding.noTransactionsDoneText.visibility = View.GONE
                    binding.recenttransaction.visibility = View.VISIBLE
                    binding.dashboardrecyclerview.visibility = View.VISIBLE
                    adapter = TransactionAdapter(requireContext(), "Dashboard", transactionlist)
                    binding.dashboardrecyclerview.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.dashboardrecyclerview.adapter = adapter
                    adapter.notifyDataSetChanged()

                    for (i in transactionlist) {
                        totalExpense += i.amount
                        editor.putString("totalExpense",totalExpense.toString())
                        editor.apply()
                        when (i.category) {
                            "Food" -> {
                                totalFood += (i.amount.toFloat())
                            }
                            "Shopping" -> {
                                totalShopping += (i.amount.toFloat())
                            }
                            "Education" -> {
                                totalEducation += (i.amount.toFloat())
                            }
                            "Others" -> {
                                totalOthers += (i.amount.toFloat())
                            }
                            "Health" -> {
                                totalHealth += (i.amount.toFloat())
                            }
                            "Transport" -> {
                                totalTransport += (i.amount.toFloat())
                            }
                        }
                    }
                    binding.budgettv.text = "$getCurrency ${totalGoal.toInt()}"
                    binding.expensetv.text = "$getCurrency ${totalExpense.toInt()}"
                    if (totalExpense > totalGoal) {
                        binding.indicator.setImageResource(R.drawable.ic_negative_transaction)
                        binding.expensetv.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

                        val notification = NotificationCompat.Builder(requireContext(),CHANNEL_ID)
                            .setSmallIcon(R.drawable.money)
                            .setContentTitle("Expense has Exceed for ${format.format(Calendar.getInstance().time)} month")
                            .setContentText("Your expenses have exceeded the monthly limit.")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .build()

                        val notificationManager = NotificationManagerCompat.from(requireContext())
                        notificationManager.notify(NOTIFICATION_ID,notification)
                    } else {
                        binding.indicator.setImageResource(R.drawable.ic_positive_amount)
                    }
                    showPiChart()
                }
            }
            .addOnFailureListener {e->
                Toast.makeText(requireContext(), ""+e.message, Toast.LENGTH_SHORT).show()
            }
    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
        // Show the bottom navigation again
        val bottomnav = requireActivity().findViewById<View>(R.id.bottomnavigation_id)
        bottomnav.visibility = View.VISIBLE
    }

    //    To show PiChart in cardview to users
    private fun showPiChart(){
        binding.piechart.clearChart()
        binding.piechart.addPieSlice(PieModel("Food",totalFood, ContextCompat.getColor(requireContext(),R.color.lightblue)))
        binding.piechart.addPieSlice(PieModel("Shopping", totalShopping, ContextCompat.getColor(requireContext(), R.color.blue)))
        binding.piechart.addPieSlice(PieModel("Transport", totalTransport, ContextCompat.getColor(requireContext(), R.color.yellow)))
        binding.piechart.addPieSlice(PieModel("Education", totalEducation, ContextCompat.getColor(requireContext(), R.color.lightBrown)))
        binding.piechart.addPieSlice(PieModel("Health", totalHealth, ContextCompat.getColor(requireContext(), R.color.green)))
        binding.piechart.addPieSlice(PieModel("Others", totalOthers, ContextCompat.getColor(requireContext(), R.color.red)))

    if(totalGoal>totalExpense){
        binding.piechart.addPieSlice(PieModel("Left",totalGoal-(totalExpense.toFloat()), ContextCompat.getColor(requireContext(), R.color.background_deep)))
    }
        binding.piechart.startAnimation()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "PDF Notifications"
            val destext = "PDF generation notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = destext
            }
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun scheduleAfternoonAlarm() {
        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        val reminderIntent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,10,reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 50)
        calendar.set(Calendar.SECOND,0)
        val alarmTime = calendar.timeInMillis

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, alarmTime,
            AlarmManager.INTERVAL_DAY, // Repeat daily
            pendingIntent
        )
    }
    fun scheduleEveningAlarm() {
        val alarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        val reminderIntent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,20,reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 1)
        calendar.set(Calendar.MINUTE, 10)
        calendar.set(Calendar.SECOND,0)
        val alarmTime = calendar.timeInMillis

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, alarmTime,
            AlarmManager.INTERVAL_DAY, // Repeat daily
            pendingIntent
        )
    }
}