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
import androidx.recyclerview.widget.RecyclerView
import com.example.spendease.R
import com.example.spendease.Model.TransactionData

class TransactionAdapter(val context: Context, val activity: Activity, val fragment: String, private val transList: List<TransactionData>):RecyclerView.Adapter<TransactionAdapter.transactionViewHolder>() {
    class  transactionViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        val title = itemView.findViewById<TextView>(R.id.titletv_id)
        val category = itemView.findViewById<TextView>(R.id.categorytv_id)
        val money = itemView.findViewById<TextView>(R.id.expensemoneytv_id)
        val date = itemView.findViewById<TextView>(R.id.datetv_id)
        val cardicon = itemView.findViewById<ImageView>(R.id.card_icon)
        val cardview = itemView.findViewById<CardView>(R.id.cardimage_id)
    }

    lateinit var userDetails: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): transactionViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_itemlist, parent, false)
        return transactionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: transactionViewHolder, position: Int) {
        val data = transList[position]
        holder.title.text = data.title
        holder.money.text = "₹"+data.amount.toInt().toString()
        holder.category.text = data.category
        holder.date.text = data.date

        when(data.category){
            "Food" ->{
                holder.cardicon.setImageResource(R.drawable.ic_baseline_fastfood_24)
                holder.cardicon.setColorFilter(ContextCompat.getColor(context,R.color.lightblue))
                holder.category.setTextColor(ContextCompat.getColor(context, R.color.lightblue))
                holder.cardview.setBackgroundColor(ContextCompat.getColor(context,R.color.cardBackground))
            }
            "Shopping"->{
                holder.cardicon.setImageResource(R.drawable.ic_baseline_shopping_cart_24)
                holder.cardicon.setColorFilter(ContextCompat.getColor(context, R.color.blue))
                holder.category.setTextColor(ContextCompat.getColor(context, R.color.blue))
                holder.cardview.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
            "Education"->{
                holder.cardicon.setImageResource(R.drawable.ic_baseline_education_24)
                holder.cardicon.setColorFilter(ContextCompat.getColor(context, R.color.lightBrown))
                holder.category.setTextColor(ContextCompat.getColor(context, R.color.lightBrown))
                holder.cardview.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
            "Transport"->{
                holder.cardicon.setImageResource(R.drawable.ic_baseline_directions_transport_24)
                holder.cardicon.setColorFilter(ContextCompat.getColor(context, R.color.yellow))
                holder.category.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                holder.cardview.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
            "Health"->{
                holder.cardicon.setImageResource(R.drawable.ic_baseline_health_24)
                holder.cardicon.setColorFilter(ContextCompat.getColor(context, R.color.green))
                holder.category.setTextColor(ContextCompat.getColor(context, R.color.green))
                holder.cardview.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
            "Others"->{
                holder.cardicon.setImageResource(R.drawable.ic_baseline_category_24)
                holder.cardicon.setColorFilter(ContextCompat.getColor(context, R.color.red))
                holder.category.setTextColor(ContextCompat.getColor(context, R.color.red))
                holder.cardview.setBackgroundColor(ContextCompat.getColor(context, R.color.cardBackground))
            }
        }

//        holder.itemView.setOnClickListener {
//            if (fragment =="Dashboard"){
//               val argument = DashboardDirections.actionDashboardToTransactionDetails()
//                Navigation.findNavController(it).navigate(argument)
//                }
//            else if(fragment == "AllTransactions"){
//                val argument = AllTransactionsDirections.actionAllTransactionsToTransactionDetails()
//                Navigation.findNavController(it).navigate(argument)
//            }
//        }
    }

    override fun getItemCount(): Int {
        return transList.size
    }


}