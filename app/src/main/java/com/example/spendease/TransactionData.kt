package com.example.spendease

import android.os.Parcelable

data class TransactionData(
    var id: Int,
    var type: String,
    var category: String,
    var title: String,
    var amount: Double,
    var date: String,
    var day: String,
    var month: String,
    var year: String,
    var note: String
)
