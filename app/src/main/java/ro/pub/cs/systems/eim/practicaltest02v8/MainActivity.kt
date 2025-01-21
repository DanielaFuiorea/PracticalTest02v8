package ro.pub.cs.systems.eim.practicaltest02v8.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ro.pub.cs.systems.eim.practicaltest02v8.R
import ro.pub.cs.systems.eim.practicaltest02v8.model.BitcoinResponse
import ro.pub.cs.systems.eim.practicaltest02v8.presenter.Presenter

class MainActivity : AppCompatActivity(), Presenter.BitcoinView {

    private lateinit var presenter: Presenter
    private lateinit var bitcoinQueryEditText: EditText
    private lateinit var getButton: Button
    private lateinit var bitcoinInfoTextView: TextView
    private lateinit var navigateToCalculatorButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = Presenter(bitcoinView = this)

        bitcoinQueryEditText = findViewById(R.id.bitcoinEditText)
        getButton = findViewById(R.id.getButton)
        bitcoinInfoTextView = findViewById(R.id.bitcoinInfoTextView)
        navigateToCalculatorButton = findViewById(R.id.navigateToCalculatorButton)

        getButton.setOnClickListener {
            val query = bitcoinQueryEditText.text.toString()
            if (query.isNotBlank()) {
                presenter.getBitcoinInfo(query)
            } else {
                Toast.makeText(this, "Please enter USD or EUR", Toast.LENGTH_SHORT).show()
            }
        }

        navigateToCalculatorButton.setOnClickListener {
            val intent = Intent(this, CalculatorActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBitcoinInfoSuccess(bitcoin: BitcoinResponse) {
        Log.d("[Bitcoin]", "Parsed Bitcoin Response: $bitcoin")
        bitcoinInfoTextView.text = when (val query = bitcoinQueryEditText.text.toString().uppercase()) {
            "USD" -> "1 Bitcoin = ${bitcoin.bpi.USD.rate} ${bitcoin.bpi.USD.code}"
            "EUR" -> "1 Bitcoin = ${bitcoin.bpi.EUR.rate} ${bitcoin.bpi.EUR.code}"
            else -> "Invalid currency"
        }
    }

    override fun onBitcoinInfoError(message: String) {
        Log.e("[Bitcoin]", message)
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }
}
