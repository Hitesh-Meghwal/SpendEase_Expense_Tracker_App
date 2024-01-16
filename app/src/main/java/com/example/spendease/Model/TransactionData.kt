package com.example.spendease.Model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.io.Serializable


data class TransactionData(

    @DocumentId
    var id : String? = null,

    @PropertyName("type")
    var type: String = "",

    @PropertyName("category")
    var category: String = "",

    @PropertyName("title")
    var title: String = "",

    @PropertyName("amount")
    var amount: Double = 0.0,

    @PropertyName("date")
    var date: String = "",

    @PropertyName("day")
    var day: Int = 0,

    @PropertyName("month")
    var month: Int = 0,

    @PropertyName("year")
    var year: Int = 0,

    @PropertyName("note")
    var note: String = ""
): Serializable
