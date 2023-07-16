package com.example.spendease.Model


data class TransactionData(

    var id: Int = 0,
    var type: String = "",
    var category: String = "",
    var title: String = "",
    var amount: Double = 0.0,
    var date: String = "",
    var day: Int = 0,
    var month: Int = 0,
    var year: Int = 0,
    var note: String = ""
)
