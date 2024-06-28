package com.example.converterbitcoin

import CoinGeckoService
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var currencySpinner: Spinner
    private lateinit var quantityEdit: EditText
    private lateinit var realTextView: TextView
    private lateinit var dolarTextView: TextView
    private lateinit var euroTextView: TextView
    private lateinit var bitcoinTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var valores: TextView
    private var descriptionTextViewResult = ""
    var quantityText = ""
    var quantityValue = 1.0
    var selectedCurrency = "brl"
    private var decimalFormat: DecimalFormat = DecimalFormat("#,###.##")
    private var decimalFormatBitcoin: DecimalFormat = DecimalFormat("#,###.#######")

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val coinGeckoService = retrofit.create(CoinGeckoService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currencySpinner = findViewById(R.id.currencySpinner)
        quantityEdit = findViewById(R.id.quantity)
        quantityEdit.setText("1")
        realTextView = findViewById(R.id.realTextView)
        dolarTextView = findViewById(R.id.dolarTextView)
        euroTextView = findViewById(R.id.euroTextView)
        bitcoinTextView = findViewById(R.id.bitcoinTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        valores = findViewById(R.id.valores)

        val btnConvert = findViewById<Button>(R.id.btnConvert)

        btnConvert.setOnClickListener {
            quantityText = quantityEdit.text.toString()
            quantityValue = quantityText!!.toDouble()
            selectCurrency()
            descriptionTextView.text = "${quantityValue.toString()} " + descriptionTextViewResult
            convertBitcoin(selectedCurrency, quantityValue!!)
        }

        mostrarValores()

    }

    private fun mostrarValores() {
        val call = coinGeckoService.getCurrencyRates("brl,usd,eur,btc")

        call.enqueue(object : Callback<Map<String, Map<String, Double>>> {
            override fun onResponse(
                call: Call<Map<String, Map<String, Double>>>,
                response: Response<Map<String, Map<String, Double>>>
            ) {
                if (response.isSuccessful) {
                    val coinGeckoResponseMap = response.body()

                    coinGeckoResponseMap?.let {
                        val bitcoinRates = it["bitcoin"]
                        bitcoinRates?.let { rates ->
                            val realRate = rates["brl"]?.toDouble() ?: 0.0
                            val dolarRate = rates["usd"]?.toDouble() ?: 0.0
                            val euroRate = rates["eur"]?.toDouble() ?: 0.0
                            val bitcoinRate = rates["btc"]?.toDouble() ?: 0.0

                            val dolarValue = (dolarRate / realRate) * quantityValue
                            val euroValue = (euroRate / realRate) * quantityValue
                            val bitcoinValue = (bitcoinRate / realRate) * quantityValue

                            updateValoresTextView(dolarValue, euroValue, bitcoinValue)
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API Error", "Error Body: $errorBody")
                    val errorCode = response.code()
                    Toast.makeText(baseContext, "$errorCode", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Log.e("MainActivity", "API Call Failure: ${t.message}")
                // Trate falhas de requisição aqui
            }
        })
        realTextView.visibility = View.VISIBLE
        dolarTextView.visibility = View.VISIBLE
        euroTextView.visibility = View.VISIBLE
        bitcoinTextView.visibility = View.VISIBLE
    }

    private fun updateValoresTextView(dolarValue: Double, euroValue: Double, bitcoinValue: Double) {
        valores.text = """
                   Valores Atualizados
                   
                1$ Dólar equivale a ${decimalFormat.format((1 / dolarValue) * quantityValue)} R$
                1€ Euro equivale a ${decimalFormat.format((1 / euroValue) * quantityValue)} R$
                1Ƀ BitCoin equivale a ${decimalFormat.format((1 / bitcoinValue) * quantityValue)} R$
    """.trimIndent()
    }

    private fun selectCurrency() {
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCurrencyValue = parent?.getItemAtPosition(position).toString()
                when (selectedCurrencyValue) {
                    "Real" -> {
                        descriptionTextViewResult = "R$ equivale a:\n\n\n"
                        selectedCurrency = "brl"
                    }

                    "Bitcoin" -> {
                        descriptionTextViewResult = "Ƀ equivale a:\n\n\n"
                        selectedCurrency = "btc"
                    }

                    "Dólar" -> {
                        descriptionTextViewResult = "$ equivale a:\n\n\n"
                        selectedCurrency = "usd"
                    }

                    "Euro" -> {
                        descriptionTextViewResult = "€ equivale a:\n\n\n"
                        selectedCurrency = "eur"
                    }

                    else -> ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }
    }

    private fun convertBitcoin(selectedCurrency: String, quantityValue: Double) {
        valores.visibility = View.GONE
        val call = coinGeckoService.getCurrencyRates("brl,usd,eur,btc")

        call.enqueue(object : Callback<Map<String, Map<String, Double>>> {
            override fun onResponse(
                call: Call<Map<String, Map<String, Double>>>,
                response: Response<Map<String, Map<String, Double>>>
            ) {
                if (response.isSuccessful) {
                    val coinGeckoResponseMap = response.body()

                    coinGeckoResponseMap?.let {
                        val bitcoinRates = it["bitcoin"]
                        bitcoinRates?.let { rates ->
                            val realRate = rates["brl"]?.toDouble() ?: 0.0
                            val dolarRate = rates["usd"]?.toDouble() ?: 0.0
                            val euroRate = rates["eur"]?.toDouble() ?: 0.0
                            val bitcoinRate = rates["btc"]?.toDouble() ?: 0.0

                            when (selectedCurrency) {
                                "brl" -> {
                                    descriptionTextView.text =
                                        "${quantityValue} R$ equivale a:\n\n\n"
                                    dolarTextView.text =
                                        "$ ${decimalFormat.format((dolarRate / realRate) * quantityValue)} Dolares"
                                    euroTextView.text =
                                        "€ ${decimalFormat.format((euroRate / realRate) * quantityValue)} Euros"
                                    bitcoinTextView.text =
                                        "Ƀ ${decimalFormatBitcoin.format((bitcoinRate / realRate) * quantityValue)} Bitcoins"
                                    realTextView.visibility = View.GONE
                                }

                                "usd" -> {
                                    realTextView.text =
                                        "$ ${decimalFormat.format((realRate / dolarRate) * quantityValue)} Reais"
                                    euroTextView.text =
                                        "€ ${decimalFormat.format((euroRate / dolarRate) * quantityValue)} Euros"
                                    bitcoinTextView.text =
                                        "Ƀ ${decimalFormatBitcoin.format((bitcoinRate / dolarRate) * quantityValue)} Bitcoins"
                                    dolarTextView.visibility = View.GONE
                                }

                                "eur" -> {
                                    realTextView.text =
                                        "€ ${decimalFormat.format((realRate / euroRate) * quantityValue)} Reais"
                                    dolarTextView.text =
                                        "$ ${decimalFormat.format((dolarRate / euroRate) * quantityValue)} Dolares"
                                    bitcoinTextView.text =
                                        "Ƀ ${decimalFormatBitcoin.format((bitcoinRate / euroRate) * quantityValue)} Bitcoins"
                                    euroTextView.visibility = View.GONE
                                }

                                "btc" -> {
                                    realTextView.text =
                                        "$ ${decimalFormat.format((realRate / bitcoinRate) * quantityValue)} Reais"
                                    dolarTextView.text =
                                        "Ƀ ${decimalFormat.format((dolarRate / bitcoinRate) * quantityValue)} Dolares"
                                    euroTextView.text =
                                        "€ ${decimalFormat.format((euroRate / bitcoinRate) * quantityValue)} Euros"
                                    bitcoinTextView.visibility = View.GONE
                                }

                                else -> "" // Trate casos não previstos conforme necessário
                            }
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API Error", "Error Body: $errorBody")
                    val errorCode = response.code()
                    Toast.makeText(baseContext, "$errorCode", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Double>>>, t: Throwable) {
                Log.e("MainActivity", "API Call Failure: ${t.message}")
                // Trate falhas de requisição aqui
            }
        })
        realTextView.visibility = View.VISIBLE
        dolarTextView.visibility = View.VISIBLE
        euroTextView.visibility = View.VISIBLE
        bitcoinTextView.visibility = View.VISIBLE
    }
}