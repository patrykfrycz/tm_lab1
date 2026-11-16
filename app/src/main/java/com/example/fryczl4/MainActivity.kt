package com.example.fryczl4

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etRealA = findViewById<EditText>(R.id.etRealA)
        val etImagA = findViewById<EditText>(R.id.etImagA)
        val etRealB = findViewById<EditText>(R.id.etRealB)
        val etImagB = findViewById<EditText>(R.id.etImagB)

        val spinnerOperation = findViewById<Spinner>(R.id.spinnerOperation)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnMultiply = findViewById<Button>(R.id.btnMultiply)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.operations_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOperation.adapter = adapter

        var isInitialSelection = true

        spinnerOperation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isInitialSelection) {
                    isInitialSelection = false
                    return
                }

                hideKeyboard()
                val inputs = validateInputs(etRealA, etImagA, etRealB, etImagB) ?: return
                val (aRe, aIm, bRe, bIm) = inputs

                when (position) {
                    0 -> { // Plus (Dodawanie)
                        val resRe = aRe + bRe
                        val resIm = aIm + bIm

                        if (!resRe.isFinite() || !resIm.isFinite()) {
                            showToast("Wynik wykracza poza dopuszczalny zakres liczbowy (overflow)!")
                            return
                        }

                        val resultString = formatComplex(resRe, resIm)
                        showToast("Wynik dodawania: $resultString")
                    }
                    1 -> { // Gwiazdka (Mnożenie)
                        val resRe = aRe * bRe - aIm * bIm
                        val resIm = aRe * bIm + aIm * bRe

                        if (!resRe.isFinite() || !resIm.isFinite()) {
                            showToast("Wynik wykracza poza dopuszczalny zakres liczbowy (overflow)!")
                            return
                        }

                        val resultString = formatComplex(resRe, resIm)
                        showToast("Wynik mnożenia: $resultString")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnAdd.setOnClickListener {
            hideKeyboard()
            val inputs = validateInputs(etRealA, etImagA, etRealB, etImagB) ?: return@setOnClickListener
            val (aRe, aIm, bRe, bIm) = inputs

            val resRe = aRe + bRe
            val resIm = aIm + bIm

            if (!resRe.isFinite() || !resIm.isFinite()) {
                showToast("Wynik wykracza poza dopuszczalny zakres liczbowy (overflow)!")
                return@setOnClickListener
            }

            val resultString = formatComplex(resRe, resIm)
            showToast("Wynik dodawania: $resultString")
        }

        btnMultiply.setOnClickListener {
            hideKeyboard()
            val inputs = validateInputs(etRealA, etImagA, etRealB, etImagB) ?: return@setOnClickListener
            val (aRe, aIm, bRe, bIm) = inputs

            // (a + bi)(c + di) = (ac - bd) + (ad + bc)i
            val resRe = aRe * bRe - aIm * bIm
            val resIm = aRe * bIm + aIm * bRe

            if (!resRe.isFinite() || !resIm.isFinite()) {
                showToast("Wynik wykracza poza dopuszczalny zakres liczbowy (overflow)!")
                return@setOnClickListener
            }

            val resultString = formatComplex(resRe, resIm)
            showToast("Wynik mnożenia: $resultString")
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    private fun validateInputs(
        etRealA: EditText,
        etImagA: EditText,
        etRealB: EditText,
        etImagB: EditText
    ): Quad<Double, Double, Double, Double>? {
        val aReStr = etRealA.text.toString().trim()
        val aImStr = etImagA.text.toString().trim()
        val bReStr = etRealB.text.toString().trim()
        val bImStr = etImagB.text.toString().trim()

        if (aReStr.isEmpty() || aImStr.isEmpty() || bReStr.isEmpty() || bImStr.isEmpty()) {
            showToast("Proszę wypełnić wszystkie pola!")
            return null
        }

        val aRe = aReStr.toDoubleOrNull()
        val aIm = aImStr.toDoubleOrNull()
        val bRe = bReStr.toDoubleOrNull()
        val bIm = bImStr.toDoubleOrNull()

        if (aRe == null || aIm == null || bRe == null || bIm == null) {
            showToast("Wprowadź poprawne liczby w polach!")
            return null
        }

        if (!aRe.isFinite() || !aIm.isFinite() || !bRe.isFinite() || !bIm.isFinite()) {
            showToast("Podane liczby są zbyt duże lub niepoprawne!")
            return null
        }

        return Quad(aRe, aIm, bRe, bIm)
    }

    fun formatComplex(re: Double, im: Double): String {
        return when {
            im == 0.0 -> "$re"
            re == 0.0 -> "${im}i"
            im > 0 -> "$re + ${im}i"
            else -> "$re - ${-im}i"
        }
    }
}

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
