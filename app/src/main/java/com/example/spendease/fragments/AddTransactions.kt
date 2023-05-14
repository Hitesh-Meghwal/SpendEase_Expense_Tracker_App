package com.example.spendease.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.spendease.R
import com.example.spendease.navigation.NavigationDrawer
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("DEPRECATION")
class AddTransactions : Fragment() {
    lateinit var bottomnav : BottomNavigationView
    lateinit var toolbar: MaterialToolbar
    lateinit var drawerLayout: DrawerLayout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_transactions, container, false)
        bottomnav = requireActivity().findViewById(R.id.bottomnavigation_id)
        toolbar = view.findViewById(R.id.newtranstoolbar_id)

        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(),R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            val action = AddTransactionsDirections.actionAddTransactionsToDashboard()
            findNavController().navigate(action)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        bottomnav.visibility = View.GONE
    }



}