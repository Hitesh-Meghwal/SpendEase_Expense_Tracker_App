package com.example.spendease.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.spendease.Model.CurrencyConversionResponse
import com.example.spendease.R
import com.example.spendease.Retrofit.RetrofitBuilder
import com.example.spendease.Retrofit.RetrofitInterface
import com.example.spendease.databinding.FragmentCurrencyConverterBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.Objects

class CurrencyConverter : Fragment() {
    lateinit var binding: FragmentCurrencyConverterBinding
    lateinit var bottomnav : BottomNavigationView
    lateinit var client: OkHttpClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCurrencyConverterBinding.inflate(layoutInflater)
        bottomnav = requireActivity().findViewById(R.id.bottomnavigation_id)

        client = OkHttpClient()

        binding.btnConvert.setOnClickListener {
            performCurrencyConversion()
        }

        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    private fun performCurrencyConversion(){
        val fromCurrency = binding.spFromCurrency.selectedItem.toString()
        val toCurrency = binding.spToCurrency.selectedItem.toString()
        val amount = binding.etFrom.text.toString().toDouble()

        val url = "https://currency-convertor-api.p.rapidapi.com/convert/$amount/$fromCurrency/$toCurrency"

        val req = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-RapidAPI-Key", "96c8183b9dmsh9ca74a28664d77ap1f405cjsndb930ca2a5ec")
            .addHeader("X-RapidAPI-Host", "currency-convertor-api.p.rapidapi.com")
            .build()

            try{
                val res = client.newCall(req).execute()
                val responseBody = res.body()?.toString()
                Log.d("ResponseBody: ","$responseBody")

                if(res.isSuccessful && !responseBody.isNullOrEmpty()){
                    val jsonObject = JSONObject(responseBody)
                    val conversionRates = jsonObject.getDouble("conversion_rate")
//                    binding.tvResult.text = convertedAmount.toString()
                }
                else{
                    Toast.makeText(requireContext(), "Amount is Unable to COnvert", Toast.LENGTH_SHORT).show()
                }
            }
            catch (e:Exception){
                Toast.makeText(requireContext(), "Error ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("Error: ","${e.message}")
            }
        }

    override fun onResume() {
        super.onResume()
        bottomnav.visibility = View.GONE
    }

}