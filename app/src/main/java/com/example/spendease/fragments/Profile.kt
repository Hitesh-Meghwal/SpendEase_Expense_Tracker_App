package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.spendease.R
import com.example.spendease.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class Profile : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    lateinit var userDetails: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        monthYearBudget()
        return binding.root
    }

    private fun monthYearBudget() {
        userDetails = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
        val email = userDetails.getString("email","").toString()
        val name = userDetails.getString("Name","").toString()
        val monthlybudget = userDetails.getString("MonthlyBudget", "").toString()
        val yearlybudget = userDetails.getString("YearlyBudget", "").toString()
        binding.profilename.text = name
        binding.monthlybudgettv.text = monthlybudget
        binding.yearlybudgettv.text = yearlybudget
        binding.email.text = email
        binding.editfab.setOnClickListener {
            openEditDialog(name,monthlybudget,yearlybudget)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    @SuppressLint("ResourceType")
    private fun openEditDialog(name : String?, monthlybudget : String?, yearlybudget : String?){
        val bottomDialog = BottomSheetDialog(requireContext(),R.style.bottom_dialog)
        bottomDialog.setContentView(R.layout.fragment_update_user_details_dialog)

        val update = bottomDialog.findViewById<Button>(R.id.updatebtn)
        val cancel = bottomDialog.findViewById<Button>(R.id.cancelbtn)
        val nameEditor = bottomDialog.findViewById<TextInputEditText>(R.id.updatename)
        val monthlyEditor = bottomDialog.findViewById<TextInputEditText>(R.id.updatemonthlybudget)
        val yearlyEditor = bottomDialog.findViewById<TextInputEditText>(R.id.updateyearlybudget)

        nameEditor?.setText(name)
        monthlyEditor?.setText(monthlybudget)
        yearlyEditor?.setText(yearlybudget)

        update?.setOnClickListener {
            val name_ = nameEditor?.text.toString()
            val monthly_budget = monthlyEditor?.text.toString()
            val yearly_budget = yearlyEditor?.text.toString()

            if (name_.isEmpty() || monthly_budget.isEmpty() || yearly_budget.isEmpty()){
                Toast.makeText(requireContext(), "Name and Budget Cannot be Empty", Toast.LENGTH_SHORT).show()
            }
            else{
                userDetails = requireActivity().getSharedPreferences("UserDetails", MODE_PRIVATE)
                val editor = userDetails.edit()
                editor.putString("Name", name_)
                editor.putString("MonthlyBudget", monthly_budget)
                editor.putString("YearlyBudget", yearly_budget)
                editor.apply()
                monthYearBudget()
                Toast.makeText(requireActivity(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                bottomDialog.dismiss()
            }
        }

        cancel?.setOnClickListener {
            bottomDialog.dismiss()
        }
        bottomDialog.show()
    }


}