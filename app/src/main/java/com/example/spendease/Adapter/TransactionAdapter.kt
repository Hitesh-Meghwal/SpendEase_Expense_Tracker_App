package com.example.spendease.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.spendease.R
import com.example.spendease.Model.TransactionData
import com.example.spendease.databinding.TransactionItemlistBinding
import com.example.spendease.fragments.AllTransactionsDirections
import com.example.spendease.fragments.DashboardDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TransactionAdapter(val context: Context, val fragment: String, private val transList: List<TransactionData>):RecyclerView.Adapter<TransactionAdapter.transactionViewHolder>() {
    class  transactionViewHolder(val binding : TransactionItemlistBinding):RecyclerView.ViewHolder(binding.root)

    lateinit var userDetails: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): transactionViewHolder {
        return transactionViewHolder(TransactionItemlistBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: transactionViewHolder, position: Int) {
        val data = transList[position]
        holder.binding.titletvId.text = data.title
        holder.binding.expensemoneytvId.text = "₹"+data.amount.toInt().toString()
        holder.binding.categorytvId.text = data.category
        holder.binding.datetvId.text = data.date

        when(data.category){
            "Food" ->{
                holder.binding.cardIcon.setImageResource(R.drawable.ic_baseline_fastfood_24)
                holder.binding.cardIcon.setColorFilter(ContextCompat.getColor(context,R.color.lightblue))
                holder.binding.categorytvId.setTextColor(ContextCompat.getColor(context, R.color.lightblue))
                holder.binding.cardimageId.setBackgroundColor(ContextCompat.getColor(context,R.color.cardBackground))
            }
            "Shopping"->{
                holder.binding.cardIcon.setImageResource(R.drawable.ic_baseline_shopping_cart_24)
                holder.binding.cardIcon.setColorFilter(ContextCompat.getColor(context, R.color.blue))
                holder.binding.categorytvId.setTextColor(ContextCompat.getColor(context, R.color.blue))
                holder.binding.cardimageId.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
            "Education"->{
                holder.binding.cardIcon.setImageResource(R.drawable.ic_baseline_education_24)
                holder.binding.cardIcon.setColorFilter(ContextCompat.getColor(context, R.color.lightBrown))
                holder.binding.categorytvId.setTextColor(ContextCompat.getColor(context, R.color.lightBrown))
                holder.binding.cardimageId.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
            "Transport"->{
                holder.binding.cardIcon.setImageResource(R.drawable.ic_baseline_directions_transport_24)
                holder.binding.cardIcon.setColorFilter(ContextCompat.getColor(context, R.color.yellow))
                holder.binding.categorytvId.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                holder.binding.cardimageId.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
            "Health"->{
                holder.binding.cardIcon.setImageResource(R.drawable.ic_baseline_health_24)
                holder.binding.cardIcon.setColorFilter(ContextCompat.getColor(context, R.color.green))
                holder.binding.categorytvId.setTextColor(ContextCompat.getColor(context, R.color.green))
                holder.binding.cardimageId.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
            "Other"->{
                holder.binding.cardIcon.setImageResource(R.drawable.ic_baseline_category_24)
                holder.binding.cardIcon.setColorFilter(ContextCompat.getColor(context, R.color.red))
                holder.binding.categorytvId.setTextColor(ContextCompat.getColor(context, R.color.red))
                holder.binding.cardimageId.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
        }

        holder.itemView.setOnClickListener {

            if (fragment =="Dashboard"){
               val argument = DashboardDirections.actionDashboardToTransactionDetails(data,fragment)
                Navigation.findNavController(it).navigate(argument)
                }
            else if(fragment == "AllTransactions"){
                val argument = AllTransactionsDirections.actionAllTransactionsToTransactionDetails(data,fragment)
                Navigation.findNavController(it).navigate(argument)
            }
        }
    }

    override fun getItemCount(): Int {
        return transList.size
    }
}