package com.example.spendease.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.midi.MidiOutputPort
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ActivityNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.example.spendease.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    lateinit var fab : FloatingActionButton
    lateinit var toolbar: MaterialToolbar
//    lateinit var drawerLayout: DrawerLayout
//    lateinit var navigationView: NavigationView
    lateinit var userDetails: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

//        Switch to AddTransaction Fragment
        fab = view.findViewById(R.id.addnewtransactions)
        fab.setOnClickListener {
            addtransactionfragment()
        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getdata()
    }

    private fun getdata(){
        val shownametv = view?.findViewById<TextView>(R.id.textView7)
        userDetails = requireActivity().getSharedPreferences("UserDetails",MODE_PRIVATE)
        val userEmail = userDetails.getString("email","")
        val getname = userDetails.getString(userEmail+"Name","")
        shownametv?.text = getname

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

    private fun addtransactionfragment(){
        val action = DashboardDirections.actionDashboardToAddTransactions()
        findNavController().navigate(action)
    }

}