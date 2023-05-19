package com.example.spendease.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spendease.TransactionData

@Dao
interface TransactionDao {

    @Query("SELECT * FROM `Transaction` ORDER BY year DESC,month DESC, day DESC, category DESC")
    fun getTransaction():LiveData<List<TransactionData>>

    @Query("SELECT * FROM `Transaction` WHERE month=:month AND year=:year")
    fun getMonthlyTransaction(month : Int, year : Int): LiveData<List<TransactionData>>

    @Query("SELECT * FROM `Transaction` WHERE year=:year")
    fun getYearlyTransaction(year: Int): LiveData<List<TransactionData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transactiondata : TransactionData)

    @Query("DELETE FROM `Transaction` WHERE id=:id")
    fun deleteTransaction(id:Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTransaction(transactiondata: TransactionData)

}