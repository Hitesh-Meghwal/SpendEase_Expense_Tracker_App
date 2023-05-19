package com.example.spendease

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Transaction")
data class TransactionData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var type: String,
    var category: String,
    var title: String,
    var amount: Double,
    var date: String,
    var day: Int,
    var month: Int,
    var year: Int,
    var note: String
)
