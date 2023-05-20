package com.example.spendease

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "Transaction")
data class TransactionData(
    @PrimaryKey(autoGenerate = true)
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
):Parcelable
