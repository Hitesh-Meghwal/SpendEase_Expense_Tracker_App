package com.example.spendease.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.spendease.Model.TransactionData
import com.example.spendease.R
import com.example.spendease.databinding.FragmentAddTransactionsBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class AddTransactions : Fragment(),View.OnClickListener {
    private lateinit var binding : FragmentAddTransactionsBinding
    val transactions by navArgs<AddTransactionsArgs>()
    private var category = ""
    lateinit var bottomnav : BottomNavigationView
    lateinit var toolbar: MaterialToolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var userDetails : SharedPreferences
    lateinit var bottomCal : BottomSheetDialog
    var day = 0
    var month = 0
    var year = 0
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentAddTransactionsBinding.inflate(inflater,container,false)
        bottomnav = requireActivity().findViewById(R.id.bottomnavigation_id)

        binding.newtranstoolbarId.navigationIcon = ContextCompat.getDrawable(requireContext(),R.drawable.baseline_arrow_back_24)
        binding.newtranstoolbarId.setNavigationOnClickListener {
            val action = AddTransactionsDirections.actionAddTransactionsToDashboard()
            findNavController().navigate(action)
        }
        setListener(binding)
        datePicker(binding)
        userDetails = requireActivity().getSharedPreferences("UserDetails",AppCompatActivity.MODE_PRIVATE)
        if(transactions.from){
            setDatas()
            binding.addtransaction.text = "Save Transaction"
            binding.newtranstoolbarId.title = "Edit Transaction"
            binding.newtranstoolbarId.setNavigationOnClickListener {
                val args = AddTransactionsDirections.actionAddTransactionsToTransactionDetails(transactions.data,"AddTransactions")
                Navigation.findNavController(binding.root).navigate(args)
            }
        }
        else{
            binding.newtranstoolbarId.setNavigationOnClickListener {
                val action = AddTransactionsDirections.actionAddTransactionsToDashboard()
                Navigation.findNavController(binding.root).navigate(action)
            }
        }
        binding.addtransaction.setOnClickListener {
            addUpdateTransaction()
        }
        binding.calculator.setOnClickListener {
            bottomCal = BottomSheetDialog(requireActivity(),R.style.bottom_dialog)
            bottomCal.setContentView(R.layout.calculator)
            bottomCal.show()
            calculator()
        }
        return binding.root
    }


    private fun setListener(binding: FragmentAddTransactionsBinding){
        binding.foodbtn.setOnClickListener(this)
        binding.transportbtn.setOnClickListener(this)
        binding.healthbtn.setOnClickListener(this)
        binding.otherbtn.setOnClickListener(this)
        binding.shoppingbtn.setOnClickListener(this)
        binding.educationbtn.setOnClickListener(this)
    }

    private fun setDatas(){
        binding.title.setText(transactions.data.title)
        binding.date.setText(transactions.data.date)
        binding.amount.setText(transactions.data.amount.toString())
        binding.note.setText(transactions.data.note)
        category = transactions.data.category
        when(category){
            "Food"->{
                setCategory(binding.foodbtn,binding.foodbtn)
            }
            "Shopping"->{
                setCategory(binding.shoppingbtn,binding.shoppingbtn)
            }
            "Transport"->{
                setCategory(binding.transportbtn,binding.transportbtn)
            }
            "Health"->{
                setCategory(binding.healthbtn,binding.healthbtn)
            }
            "Other"->{
                setCategory(binding.otherbtn,binding.otherbtn)
            }
            "Education"->{
                setCategory(binding.educationbtn,binding.educationbtn)
            }
        }
    }
    private fun addUpdateTransaction(){
        val firestore = FirebaseFirestore.getInstance()
        val title = binding.title.text.toString()
        val date = binding.date.text.toString().trim()
        val amount = binding.amount.text.toString().trim()
        val note = binding.note.text.toString()
        if (title.isEmpty() || date.isEmpty() || amount.isEmpty() || category == ""){
            notifyUser("Enter all required details")
        }
        else {
            if (transactions.from) {
                val updates = hashMapOf<String, Any>(
                    "category" to category,
                    "title" to title,
                    "amount" to amount.toDouble(),
                    "date" to date,
                    "day" to day,
                    "month" to month,
                    "year" to year,
                    "note" to note
                )

                val documentId = transactions.data.id ?:""
                firestore.collection("Transactions")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .collection("TransactionList")
                    .document(documentId)
                    .update(updates)
                    .addOnSuccessListener {
                        notifyUser("Transaction Updated Successfully")
//                        val action = AddTransactionsDirections.actionAddTransactionsToTransactionDetails(transactions.data,"AddTransaction")
                        val action = AddTransactionsDirections.actionAddTransactionsToDashboard()
                        Navigation.findNavController(binding.root).navigate(action)
                    }
                    .addOnFailureListener {
                        notifyUser("Something went wrong" + it.message)
                    }
            }
            else {
                val newTransactions = TransactionData(
                    null,
                    "Expense",
                    category,
                    title,
                    amount.toDouble(),
                    date,
                    day,
                    month,
                    year,
                    note
                )
                firestore.collection("Transactions")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .collection("TransactionList")
                    .add(newTransactions)
                    .addOnSuccessListener {documentref->
                        val newdocumentid = documentref.id
                        newTransactions.id = newdocumentid
                        notifyUser("Transaction Added Successfully")
                        val action = AddTransactionsDirections.actionAddTransactionsToDashboard()
                        findNavController().navigate(action)
                    }
                    .addOnFailureListener { e ->
                        notifyUser("Something went wrong" + e.message)
                    }
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    private fun datePicker(binding: FragmentAddTransactionsBinding){
        val cal = Calendar.getInstance()
        binding.date.setText(SimpleDateFormat("dd MMMM YYYY").format(System.currentTimeMillis()))
        day = SimpleDateFormat("dd").format(System.currentTimeMillis()).toInt()
        month = SimpleDateFormat("MM").format(System.currentTimeMillis()).toInt()
        year = SimpleDateFormat("YYYY").format(System.currentTimeMillis()).toInt()
        val dateSetListener = OnDateSetListener{_,Year,monthOfYear,dayOfMonth->
            cal.set(Calendar.YEAR,Year)
            cal.set(Calendar.MONTH,monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            var myFormat = "dd MMMM YYYY"
            var simpleDateFormat = SimpleDateFormat(myFormat, Locale.US)
            binding.date.setText(simpleDateFormat.format(cal.time))
            myFormat = "dd"
            simpleDateFormat = SimpleDateFormat(myFormat,Locale.US)
            day = simpleDateFormat.format(cal.time).toInt()

            myFormat = "MM"
            simpleDateFormat = SimpleDateFormat(myFormat,Locale.US)
            month = simpleDateFormat.format(cal.time).toInt()

            myFormat="YYYY"
            simpleDateFormat = SimpleDateFormat(myFormat,Locale.US)
            year = simpleDateFormat.format(cal.time).toInt()
        }
        binding.date.setOnClickListener {
            DatePickerDialog(requireContext(),dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

    }
    override fun onClick(v:View){
        when(v){
            binding.foodbtn ->{
                setCategory(v,binding.foodbtn)
            }
            binding.shoppingbtn->{
                setCategory(v,binding.shoppingbtn)
            }
            binding.educationbtn->{
                setCategory(v,binding.educationbtn)
            }
            binding.transportbtn->{
                setCategory(v,binding.transportbtn)
            }
            binding.otherbtn->{
                setCategory(v,binding.otherbtn)
            }
            binding.healthbtn->{
                setCategory(v,binding.healthbtn)
            }

        }
    }
    @SuppressLint("PrivateResource")
    private fun setCategory(v: View, button:MaterialButton){
        category = button.text.toString()
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), com.google.android.material.R.color.mtrl_btn_text_btn_bg_color_selector))
        button.setIconTintResource(R.color.bottomNav)
        button.setStrokeColorResource(R.color.black)
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.bottomNav))
        when(v){
            binding.foodbtn->{
                removeBackground(binding.shoppingbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.healthbtn)
            }
            binding.shoppingbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.healthbtn)
            }
            binding.transportbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.shoppingbtn)
                removeBackground(binding.healthbtn)
            }
            binding.otherbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.shoppingbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.healthbtn)
            }
            binding.educationbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.shoppingbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.healthbtn)
            }
            binding.healthbtn->{
                removeBackground(binding.foodbtn)
                removeBackground(binding.educationbtn)
                removeBackground(binding.otherbtn)
                removeBackground(binding.transportbtn)
                removeBackground(binding.shoppingbtn)
            }

        }
    }
    private fun removeBackground(button: MaterialButton){
        button.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.transparent))
        button.setIconTintResource(R.color.textSecondary)
        button.setStrokeColorResource(R.color.textSecondary)
        button.setTextColor(ContextCompat.getColor(requireContext(),R.color.textSecondary))
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        bottomnav.visibility = View.GONE
    }
    @SuppressLint("SetTextI18n")
    private fun calculator(){
        var number : String
        val input = bottomCal.findViewById<TextView>(R.id.input)
        val result = bottomCal.findViewById<TextView>(R.id.result)
        val one = bottomCal.findViewById<MaterialButton>(R.id.one)
        val two = bottomCal.findViewById<MaterialButton>(R.id.two)
        val three = bottomCal.findViewById<MaterialButton>(R.id.three)
        val four = bottomCal.findViewById<MaterialButton>(R.id.four)
        val five = bottomCal.findViewById<MaterialButton>(R.id.five)
        val six = bottomCal.findViewById<MaterialButton>(R.id.six)
        val seven = bottomCal.findViewById<MaterialButton>(R.id.seven)
        val eight = bottomCal.findViewById<MaterialButton>(R.id.eight)
        val nine = bottomCal.findViewById<MaterialButton>(R.id.nine)
        val zero = bottomCal.findViewById<MaterialButton>(R.id.zero)
        val doubleZero = bottomCal.findViewById<MaterialButton>(R.id.doublezero)
        val equalsto = bottomCal.findViewById<MaterialButton>(R.id.equal)
        val plus = bottomCal.findViewById<MaterialButton>(R.id.plus)
        val minus = bottomCal.findViewById<MaterialButton>(R.id.minus)
        val mul = bottomCal.findViewById<MaterialButton>(R.id.mul)
        val divide = bottomCal.findViewById<MaterialButton>(R.id.divide)
        val mod = bottomCal.findViewById<MaterialButton>(R.id.mod)
        val clear = bottomCal.findViewById<MaterialButton>(R.id.clear)
        val delete = bottomCal.findViewById<MaterialButton>(R.id.delete)
        val dot = bottomCal.findViewById<MaterialButton>(R.id.dot)

        val donebtn = bottomCal.findViewById<MaterialButton>(R.id.donebtn)
        donebtn?.setOnClickListener {
            number = result?.text.toString()
            binding.amount.setText(number)
            bottomCal.dismiss()
        }
        one?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "1"
        }
        two?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "2"
        }
        three?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "3"
        }
        four?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "4"
        }
        five?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "5"
        }
        six?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "6"
        }
        seven?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "7"
        }
        eight?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "8"
        }
        nine?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "9"
        }
        zero?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "0"
        }
        doubleZero?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "00"
        }
        minus?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "-"
        }
        plus?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "+"
        }
        divide?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "/"
        }
        mul?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "*"
        }
        mod?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "%"
        }
        dot?.setOnClickListener {
            number = input?.text.toString()
            input?.text = number + "."
        }
        clear?.setOnClickListener {
            number = ""
            input?.text = number
            result?.text = number
        }
        delete?.setOnClickListener {
            number = input?.text.toString()
            if (number.isNotEmpty()){
                val newInput = number.substring(0,number.length - 1)
                input?.text = newInput
            }
        }
        equalsto?.setOnClickListener {
            val expression = ExpressionBuilder(input?.text.toString()).build()
            val output = expression.evaluate()
            result?.text = output.toString()

        }
    }
    private fun notifyUser(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}