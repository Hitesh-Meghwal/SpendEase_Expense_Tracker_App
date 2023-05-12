package com.example.spendease.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.spendease.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel

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
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var userDetails: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
//        drawerLayout = requireActivity().findViewById(R.id.drawerlayout)
//        navigationView = requireActivity().findViewById(R.id.navigation_drawer)
//        val bottomnav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomnavigation_id)
//        bottomnav.visibility = View.VISIBLE
        return view


    }


//    To show PiChart in cardview to users
    private fun showPiChart(){

    pieChart = requireView().findViewById(R.id.piechart)
    pieChart.addPieSlice(PieModel("Food",totalFood, ContextCompat.getColor(requireContext(),R.color.yellow)))
    pieChart.addPieSlice(PieModel("Shopping", totalShopping, ContextCompat.getColor(requireContext(), R.color.blue)))
    pieChart.addPieSlice(PieModel("Transport", totalTransport, ContextCompat.getColor(requireContext(), R.color.lightBrown)))
    pieChart.addPieSlice(PieModel("Education", totalEducation, ContextCompat.getColor(requireContext(), R.color.green)))
    pieChart.addPieSlice(PieModel("Health", totalHealth, ContextCompat.getColor(requireContext(), R.color.lightblue)))
    pieChart.addPieSlice(PieModel("Others", totalOthers, ContextCompat.getColor(requireContext(), R.color.red)))

    if(totalGoal>totalExpense){
        pieChart.addPieSlice(PieModel("Left",totalGoal-(totalExpense.toFloat()), ContextCompat.getColor(requireContext(), R.color.background_deep)))
    }
    pieChart.startAnimation()
    }

}