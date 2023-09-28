package com.example.spendease.Model

data class CurrencyConversionResponse(
    val amount: Double,
    val from: String,
    val to: String,
    val rate: Double
)