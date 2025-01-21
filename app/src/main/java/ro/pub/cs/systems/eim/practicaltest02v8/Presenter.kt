package ro.pub.cs.systems.eim.practicaltest02v8.presenter

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import ro.pub.cs.systems.eim.practicaltest02v8.model.BitcoinResponse
import com.google.gson.Gson

class Presenter(
    private val bitcoinView: BitcoinView? = null,
    private val calculatorView: CalculatorView? = null
) {

    interface BitcoinView {
        fun onBitcoinInfoSuccess(bitcoin: BitcoinResponse)
        fun onBitcoinInfoError(message: String)
    }

    interface CalculatorView {
        fun onCalculationSuccess(result: String)
        fun onCalculationError(message: String)
    }

    private val client = OkHttpClient()
    private var lastUpdate: Long = 0
    private var cachedResponse: BitcoinResponse? = null
    private val cacheDuration = 20000 // Cache valid for 20 seconds

    fun getBitcoinInfo(currency: String) {
        val currentTime = System.currentTimeMillis()

        // Utilizează datele din cache dacă nu au expirat
        if (cachedResponse != null && currentTime - lastUpdate < cacheDuration) {
            Log.d("[Bitcoin]", "Using cached data")
            bitcoinView?.onBitcoinInfoSuccess(cachedResponse!!)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) throw Exception("HTTP error ${response.code}")

                val responseData = response.body?.string() ?: throw Exception("No response body")
                Log.d("[Bitcoin]", "API Response: $responseData")

                val bitcoinResponse = Gson().fromJson(responseData, BitcoinResponse::class.java)

                // Cachează răspunsul
                cachedResponse = bitcoinResponse
                lastUpdate = currentTime
                Log.d("[Bitcoin]", "Data cached at: $lastUpdate")

                CoroutineScope(Dispatchers.Main).launch {
                    bitcoinView?.onBitcoinInfoSuccess(bitcoinResponse)
                }
            } catch (e: Exception) {
                Log.e("[Bitcoin]", "Error fetching Bitcoin info", e)
                CoroutineScope(Dispatchers.Main).launch {
                    bitcoinView?.onBitcoinInfoError(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun calculate(operation: String, operand1: String, operand2: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "http://localhost:8080//expr_get.php?operation=$operation&t1=$operand1&t2=$operand2"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) throw Exception("HTTP error ${response.code}")

                val result = response.body?.string() ?: throw Exception("No response body")

                Log.d("[Calculator]", "Response: $result")

                CoroutineScope(Dispatchers.Main).launch {
                    calculatorView?.onCalculationSuccess(result)
                }
            } catch (e: Exception) {
                Log.e("[Calculator]", "Error: ${e.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    calculatorView?.onCalculationError(e.message ?: "Unknown error")
                }
            }
        }
    }
}
