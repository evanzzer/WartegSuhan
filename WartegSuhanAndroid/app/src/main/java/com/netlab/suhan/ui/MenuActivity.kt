package com.netlab.suhan.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.netlab.suhan.databinding.ActivityMenuBinding
import com.netlab.suhan.ui.customer.CustomerActivity
import com.netlab.suhan.ui.stock.StockActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    companion object {
        private const val SHARED_PREF_URL = "com.netlab.suhan"
        private const val SP_URL_KEY = "url"
        var URL = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(SHARED_PREF_URL, Context.MODE_PRIVATE)
        val url = sharedPreferences.getString(SP_URL_KEY, "10.0.2.2:8080")
        binding.etURL.setText(url)

        URL = "http://$url"

        binding.btnEditURL.setOnClickListener {
            with(binding) {
                btnEditURL.visibility = View.INVISIBLE
                btnSaveURL.visibility = View.VISIBLE
                etURL.isEnabled = true
                etURL.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }

        binding.btnSaveURL.setOnClickListener {
            with(binding) {
                btnEditURL.visibility = View.VISIBLE
                btnSaveURL.visibility = View.INVISIBLE
                etURL.isEnabled = false
            }

            sharedPreferences.edit {
                val newUrl = binding.etURL.text.toString()
                putString(SP_URL_KEY, newUrl)
                URL = "http://$newUrl"
            }
        }

        binding.btnListStock.setOnClickListener {
            startActivity(Intent(this, StockActivity::class.java))
        }

        binding.btnListCustomer.setOnClickListener {
            startActivity(Intent(this, CustomerActivity::class.java))
        }

        binding.btnCashier.setOnClickListener {
            startActivity(Intent(this, CashierActivity::class.java))
        }

        binding.btnTopUp.setOnClickListener {
            startActivity(Intent(this, TopUpActivity::class.java))
        }

        binding.btnOrderHistory.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }
    }
}