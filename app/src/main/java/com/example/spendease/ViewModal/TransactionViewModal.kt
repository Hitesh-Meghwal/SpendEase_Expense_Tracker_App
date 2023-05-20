package com.example.spendease.ViewModal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.spendease.Repository.TransactionRepository
import com.example.spendease.TransactionData
import com.example.spendease.TransactionDatabase.TransactionDatabase

class TransactionViewModal(application:Application) : AndroidViewModel(application) {

    private val repository : TransactionRepository

    init {
        val dao = TransactionDatabase.getDatabaseInstance(application).getTransactionDao()
        repository = TransactionRepository(dao)

    }

    fun addTransaction(transactionData: TransactionData){
        repository.insertTransaction(transactionData)
    }

    fun getTransaction():LiveData<List<TransactionData>> = repository.getAllTransactions()

    fun getMonthlyTransaction(month: Int, year:Int):LiveData<List<TransactionData>> = repository.getMonthlyTransaction(month,year)

    fun getYearlyTransaction(year:Int):LiveData<List<TransactionData>> = repository.getYearlyTransaction(year)

    fun deleteTransaction(id:Int){
        repository.deleteTransaction(id)
    }

    fun updateTransaction(transactionData: TransactionData){
        repository.updateTransaction(transactionData)
    }



}