package com.example.spendease

data class TransactionData(
    var id: Int?,
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
