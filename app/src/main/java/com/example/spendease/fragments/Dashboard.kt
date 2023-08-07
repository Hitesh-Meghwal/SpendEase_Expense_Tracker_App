package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spendease.Adapter.TransactionAdapter
import com.example.spendease.Model.TransactionData
import com.example.spendease.R
import com.example.spendease.databinding.FragmentDashboardBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
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
    lateinit var fab : FloatingActionButton
    lateinit var toolbar: MaterialToolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
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

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.transactionRecyclerView)
        getdata()
        return binding.root

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getdata(){

        userDetails = requireActivity().getSharedPreferences("UserDetails",MODE_PRIVATE)
        val getname = userDetails.getString("Name","")
        val getMonthlyBudget = userDetails.getString("MonthlyBudget","")
        val getCurrency = userDetails.getString("Currency","")

        val formatmonth = SimpleDateFormat("MM")
        val currentMonth = formatmonth.format(Calendar.getInstance().time)
        val formatyear = SimpleDateFormat("YYYY")
        val currentYear = formatyear.format(Calendar.getInstance().time)
        val format = SimpleDateFormat("MMMM")
        binding.datetv.text = "${format.format(Calendar.getInstance().time)} $currentYear"
//        Greeting to user
        val calendar = Calendar.getInstance()
        val hourrofday = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when(hourrofday){
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
                if(!it.isEmpty){
                    for(data in it.documents){
                        val transaction = data.toObject(TransactionData::class.java)
                        transaction?.let {
                            if(!transactionlist.contains(it)){
                                transactionlist.add(it) }
                        }
                    }
                }

                if(transactionlist.isEmpty()){
                    binding.noTransactionsDoneText.text = "Add Your First Transaction of ${currentMonth.format(Calendar.getInstance().time)} $currentYear \n Click On + to add Transaction"
                    binding.noTransactionsDoneText.visibility = View.VISIBLE
                    binding.recenttransaction.visibility = View.GONE
                }
                else{
                    binding.noTransactionsDoneText.visibility = View.GONE
                    binding.recenttransaction.visibility = View.VISIBLE
                    binding.transactionRecyclerView.visibility = View.VISIBLE
                    adapter = TransactionAdapter(requireContext(),"Dashboard", transactionlist)
                    binding.transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.transactionRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }


            for (i in transactionlist) {
                totalExpense += i.amount
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

            val monthlyBudget = requireActivity().findViewById<TextView>(R.id.budgettv)
            val myexpense = requireActivity().findViewById<TextView>(R.id.expensetv)
            monthlyBudget.text = "$getCurrency ${totalGoal.toInt()}"
            myexpense.text = "$getCurrency ${totalExpense.toInt()}"
            val indicator = requireActivity().findViewById<ImageView>(R.id.indicator)
            if (totalExpense > totalGoal) {
                indicator.setImageResource(R.drawable.ic_negative_transaction)
                myexpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            } else {
                indicator.setImageResource(R.drawable.ic_positive_amount)
            }
            showPiChart()
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

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition  //taking positions of views
            if(direction == ItemTouchHelper.RIGHT && pos >= 0 && pos < transactionlist.size){
                val recentlyDeletedTransaction = transactionlist.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                //after removing from recyclerview it takes id and delete that item from firebase!
                val transactionId = recentlyDeletedTransaction.id
                if (transactionId!=null){
                    deleteTransactions(transactionId)
                }
                val snackbar = Snackbar.make(binding.root,"Transaction Deleted!",Snackbar.LENGTH_SHORT)
                snackbar.setAction("Undo"){
                    //Restore the deleted data
                    handleUndoAction(recentlyDeletedTransaction,pos)
                }
                snackbar.show()
            }
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                .addSwipeRightBackgroundColor(Color.RED)
                .addSwipeRightActionIcon(R.drawable.baseline_delete_24)
                .addSwipeRightLabel("Delete")
                .setSwipeRightLabelTextSize(1,18f)
                .setSwipeRightLabelColor(Color.BLACK)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun handleUndoAction(recentlyDeletedTransaction : TransactionData?,pos : Int){
        if(recentlyDeletedTransaction!=null) {
            transactionlist.add(pos, recentlyDeletedTransaction)
            adapter.notifyItemInserted(pos)
            val transactionId = recentlyDeletedTransaction.id

            if (transactionId != null) {
                firestore.collection("Transactions")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .collection("TransactionList")
                    .document(transactionId)
                    .set(recentlyDeletedTransaction)
                    .addOnSuccessListener {
                        getdata()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to Restored Transaction ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    private fun deleteTransactions(transactionId : String){
        firestore.collection("Transactions")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("TransactionList")
            .document(transactionId)
            .delete()
            .addOnSuccessListener {
                getdata()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to Delete Transaction ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}