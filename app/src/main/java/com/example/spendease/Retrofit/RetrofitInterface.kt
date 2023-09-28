package com.example.spendease.Retrofit

import com.example.spendease.Model.CurrencyConversionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {
   @Headers(
      "X-RapidAPI-Key: 96c8183b9dmsh9ca74a28664d77ap1f405cjsndb930ca2a5ec",
      "X-RapidAPI-Host: currency-convertor-api.p.rapidapi.com"
   )
   @GET("convert/{amount}/{fromCurrency}/{toCurrency}")
   fun convertCurrency(
      @Path("amount") amount: Double,
      @Path("fromCurrency") fromCurrency: String,
      @Path("toCurrency") toCurrency: String
   ): Call<CurrencyConversionResponse>
}