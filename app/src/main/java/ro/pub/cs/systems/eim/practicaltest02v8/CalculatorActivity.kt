package ro.pub.cs.systems.eim.practicaltest02v8.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ro.pub.cs.systems.eim.practicaltest02v8.R
import ro.pub.cs.systems.eim.practicaltest02v8.presenter.Presenter

class CalculatorActivity : AppCompatActivity(), Presenter.CalculatorView {

    private lateinit var presenter: Presenter
    private lateinit var operand1EditText: EditText
    private lateinit var operand2EditText: EditText
    private lateinit var operationEditText: EditText
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        presenter = Presenter(calculatorView = this)

        operand1EditText = findViewById(R.id.operand1EditText)
        operand2EditText = findViewById(R.id.operand2EditText)
        operationEditText = findViewById(R.id.operationEditText)
        calculateButton = findViewById(R.id.calculateButton)
        resultTextView = findViewById(R.id.resultTextView)

        calculateButton.setOnClickListener {
            val operand1 = operand1EditText.text.toString()
            val operand2 = operand2EditText.text.toString()
            val operation = operationEditText.text.toString()

            if (operand1.isNotBlank() && operand2.isNotBlank() && operation.isNotBlank()) {
                presenter.calculate(operation, operand1, operand2)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCalculationSuccess(result: String) {
        Log.d("[Calculator]", "Result: $result")
        resultTextView.text = result
    }

    override fun onCalculationError(message: String) {
        Log.e("[Calculator]", "Error: $message")
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }
}
