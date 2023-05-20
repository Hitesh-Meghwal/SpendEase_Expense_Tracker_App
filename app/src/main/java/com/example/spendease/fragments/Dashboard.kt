package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendease.Adapter.TransactionAdapter
import com.example.spendease.R
import com.example.spendease.TransactionData
import com.example.spendease.ViewModal.TransactionViewModal
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.text.SimpleDateFormat
import java.util.Calendar

class Dashboard : Fragment() {
    lateinit var pieChart: PieChart
    private var totalExpense = 0.0
    private var totalGoal = 5000.0f
    private var totalFood = 0.0f
    private var totalShopping = 0.0f
    private var totalTransport = 0.0f
    private var totalHealth = 0.0f
    private var totalEducation = 0.0f
    private var totalOthers = 0.0f
    private val viewModal : TransactionViewModal by viewModels()
    lateinit var fab : FloatingActionButton
    lateinit var toolbar: MaterialToolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var userDetails: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

//        Switch to AddTransaction Fragment
        fab = view.findViewById(R.id.addnewtransactions)
//        navigationView = requireActivity().findViewById(R.id.navigation_drawer)
        val args = DashboardDirections.actionDashboardToAddTransactions(TransactionData(null,"","","",0.0,"",0,0,0,""),false)
        fab.setOnClickListener {
            Navigation.findNavController(view).navigate(args)
        }
        getdata()
        return view

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getdata(){
        val recenttransactiontxt = requireActivity().findViewById<TextView>(R.id.text1)
        val noTransationtext = requireActivity().findViewById<TextView>(R.id.noTransactionsDoneText)
        val transrecyclerview = requireActivity().findViewById<RecyclerView>(R.id.transactionrecyclerview)

        userDetails = requireActivity().getSharedPreferences("UserDetails",MODE_PRIVATE)
        val getname = userDetails.getString("Name","")
        val getMonthlyBudget = userDetails.getString("MonthlyBudget","")

        val formatmonth = SimpleDateFormat("MM")
        val currentMonth = formatmonth.format(Calendar.getInstance().time)
        val formatyear = SimpleDateFormat("YYYY")
        val currentYear = formatyear.format(Calendar.getInstance().time)
        val format = SimpleDateFormat("MMMM")
        val datatv = view?.findViewById<TextView>(R.id.datetv)
        datatv?.text = "${format.format(Calendar.getInstance().time)} $currentYear"
        val nametv = view?.findViewById<TextView>(R.id.textView7)
        nametv?.text = "Hi $getname !!"

        totalExpense = 0.0
        totalGoal = getMonthlyBudget?.toFloat()!!
        totalFood = 0.0f
        totalHealth = 0.0f
        totalEducation = 0.0f
        totalOthers = 0.0f
        totalShopping = 0.0f
        totalTransport = 0.0f

//        viewModal = ViewModelProvider(this).get(TransactionViewModal::class.java)
        viewModal.getMonthlyTransaction(currentMonth.toInt(),currentYear.toInt()).observe(viewLifecycleOwner){ transactionList ->
            if (transactionList.isEmpty()){
                noTransationtext.text = "Add Your First Transaction of ${formatmonth.format(Calendar.getInstance().time)} $currentYear \n Click On + to add Transactions"
                noTransationtext.visibility = View.VISIBLE
                noTransationtext.visibility = View.GONE
                recenttransactiontxt.visibility = View.GONE
            }
            else{
                recenttransactiontxt.visibility = View.VISIBLE
                noTransationtext.visibility = View.GONE
                transrecyclerview.visibility = View.VISIBLE
            }
            transrecyclerview.layoutManager = LinearLayoutManager(requireContext())
            transrecyclerview.adapter = TransactionAdapter(requireContext(),requireActivity(),"Dashboard",transactionList.reversed())

            for (i in transactionList){
                totalExpense += i.amount
                when(i.category){
                    "Food" ->{
                        totalFood += (i.amount.toFloat())
                    }
                    "Shopping"->{
                        totalShopping += (i.amount.toFloat())
                    }
                    "Education"->{
                        totalEducation += (i.amount.toFloat())
                    }
                    "Others"->{
                        totalOthers += (i.amount.toFloat())
                    }
                    "Health"->{
                        totalHealth += (i.amount.toFloat())
                    }
                    "Transport"->{
                        totalTransport += (i.amount.toFloat())
                    }
                }
            }

            val monthlyBudget = requireActivity().findViewById<TextView>(R.id.budgettv)
            val myexpense = requireActivity().findViewById<TextView>(R.id.expensetv)
            monthlyBudget.text = "₹${totalGoal.toInt()}"
            myexpense.text = "₹${totalExpense.toInt()}"
            val indicator = requireActivity().findViewById<ImageView>(R.id.indicator)
            if(totalExpense > totalGoal){
                indicator.setImageResource(R.drawable.ic_negative_transaction)
                myexpense.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
            }
            else{
                indicator.setImageResource(R.drawable.ic_positive_amount)
            }
            showPiChart()
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

    pieChart = requireView().findViewById(R.id.piechart)
    pieChart.addPieSlice(PieModel("Food",totalFood, ContextCompat.getColor(requireContext(),R.color.lightblue)))
    pieChart.addPieSlice(PieModel("Shopping", totalShopping, ContextCompat.getColor(requireContext(), R.color.blue)))
    pieChart.addPieSlice(PieModel("Transport", totalTransport, ContextCompat.getColor(requireContext(), R.color.yellow)))
    pieChart.addPieSlice(PieModel("Education", totalEducation, ContextCompat.getColor(requireContext(), R.color.lightBrown)))
    pieChart.addPieSlice(PieModel("Health", totalHealth, ContextCompat.getColor(requireContext(), R.color.green)))
    pieChart.addPieSlice(PieModel("Others", totalOthers, ContextCompat.getColor(requireContext(), R.color.red)))

    if(totalGoal>totalExpense){
        pieChart.addPieSlice(PieModel("Left",totalGoal-(totalExpense.toFloat()), ContextCompat.getColor(requireContext(), R.color.background_deep)))
    }
    pieChart.startAnimation()
    }


}