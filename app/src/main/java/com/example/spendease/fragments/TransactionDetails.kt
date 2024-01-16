package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.spendease.R
import com.example.spendease.databinding.FragmentTransactionDetailsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class TransactionDetails : Fragment() {
    val transaction by navArgs<TransactionDetailsArgs>()
    private lateinit var binding : FragmentTransactionDetailsBinding
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionDetailsBinding.inflate(inflater,container,false)
        val bottomnav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomnavigation_id)
        val userDetails = requireActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        bottomnav.visibility = View.GONE
        val currencySymbol = userDetails.getString("Currency","")

        binding.title.text = transaction.data.title
        binding.amount.text = "$currencySymbol ${transaction.data.amount}"
        binding.category.text = transaction.data.category
        binding.date.text = transaction.data.date
        binding.note.text = transaction.data.note

        binding.toolbarId.navigationIcon = ContextCompat.getDrawable(requireContext(),R.drawable.baseline_arrow_back_24)
        // Navigate back to the appropriate destination based on the "fragment" argument
//        val animation = AnimationUtils.loadAnimation(context, R.anim.back_pressed_animation)
        binding.toolbarId.setNavigationOnClickListener {
//            binding.toolbarId.startAnimation(animation)
            if(transaction.fragment == "Dashboard"){
                Navigation.findNavController(binding.root).navigate(R.id.action_transactionDetails_to_dashboard)
            }
            else if(transaction.fragment == "AllTransactions"){
                Navigation.findNavController(binding.root).navigate(R.id.action_transactionDetails_to_allTransactions)
            }
            else{
                Navigation.findNavController(binding.root).navigate(R.id.action_transactionDetails_to_dashboard)
            }
        }

        binding.edit.setOnClickListener {
            val arg = TransactionDetailsDirections.actionTransactionDetailsToAddTransactions(transaction.data,true)
            Navigation.findNavController(binding.root).navigate(arg)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

}