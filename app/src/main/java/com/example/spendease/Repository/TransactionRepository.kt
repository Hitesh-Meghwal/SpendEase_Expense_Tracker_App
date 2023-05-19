package com.example.spendease.Repository

import androidx.lifecycle.LiveData
import com.example.spendease.Dao.TransactionDao
import com.example.spendease.TransactionData

class TransactionRepository(private val dao: TransactionDao) {

    fun getAllTransactions():LiveData<List<TransactionData>>{
        return dao.getTransaction()
    }

    fun getMonthlyTransaction(month:Int,year:Int):LiveData<List<TransactionData>>{
        return dao.getMonthlyTransaction(month,year)
    }

    fun getYearlyTransaction(year:Int):LiveData<List<TransactionData>>{
        return dao.getYearlyTransaction(year)
    }

    fun insertTransaction(transactionData: TransactionData){
        return dao.insertTransaction(transactionData)
    }

    fun deleteTransaction(id:Int){
        return dao.deleteTransaction(id)
    }

    fun updateTransaction(transactionData: TransactionData){
        return dao.updateTransaction(transactionData)
    }

}