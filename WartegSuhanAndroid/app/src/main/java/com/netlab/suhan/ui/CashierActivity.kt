package com.netlab.suhan.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.netlab.suhan.R
import com.netlab.suhan.data.Menu
import com.netlab.suhan.databinding.ActivityCashierBinding
import com.netlab.suhan.ui.customer.DialogCustomer
import com.netlab.suhan.ui.stock.DialogItem
import com.netlab.suhan.utils.PriceFormatter
import com.netlab.suhan.utils.WebRequest

class CashierActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCashierBinding
    private var custId = 0
    private var menuId = 0
    private lateinit var menu: Menu
    private var total: Double = 0.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.kasir)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.layoutCustomerID.setEndIconOnClickListener {
            val dialog = DialogCustomer()
            dialog.onDataSelected = { data ->
                custId = data.id
                binding.etCustomerID.setText(custId.toString())
            }
            dialog.show(supportFragmentManager, null)
        }

        binding.btnNewCustomer.setOnClickListener {
            val responseListener = Response.Listener<String> { response ->
                if (response != null && response.isNotBlank()) {
                    custId = response.toInt()
                    binding.etCustomerID.setText(custId.toString())
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Terdapat kesalahan dalam membaca ID Customer terbaru")
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                }
            }

            val errorListener = Response.ErrorListener { error ->
                val errorCode = error.networkResponse.statusCode

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Error Code $errorCode.")
                    .setPositiveButton("OK") { _, _ -> finish() }
                    .show()
            }

            val queue = Volley.newRequestQueue(this)
            val request = WebRequest(Request.Method.GET, MenuActivity.URL + "/customer/new", responseListener, errorListener, HashMap())
            queue.add(request)
        }

        binding.layoutMenu.setEndIconOnClickListener {
            val dialog = DialogItem()
            dialog.onDataSelected = { data ->
                menu = data
                menuId = data.id
                binding.etMenu.setText(data.name)
                if (binding.etStock.text.toString().isBlank()) binding.etStock.setText("1")
                binding.etHarga.setText(PriceFormatter.format(data.price))
            }
            dialog.show(supportFragmentManager, null)
        }

        binding.btnCount.setOnClickListener {
            if (checkRequirement() && menuId != 0) {
                try {
                    val price = menu.price
                    with(binding) {
                        btnCount.visibility = View.GONE
                        lblTotal.visibility = View.VISIBLE
                        tvTotal.visibility = View.VISIBLE
                        btnOrder.visibility = View.VISIBLE
                        btnNewCustomer.visibility = View.GONE

                        layoutCustomerID.isEndIconVisible = false
                        layoutMenu.isEndIconVisible = false
                        etStock.isEnabled = false

                        total = etStock.text.toString().toInt() * price
                        tvTotal.text = "Rp${PriceFormatter.format(total)},00"
                    }
                } catch (e: UninitializedPropertyAccessException) {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Terdapat kesalahan membaca data menu")
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                }
            }
        }

        binding.btnOrder.setOnClickListener {
            val responseListener = Response.Listener<String> { response ->
                if (response != null && response.isNotBlank()) {
                    AlertDialog.Builder(this)
                        .setTitle("Informasi")
                        .setMessage("Data berhasil ditambahkan ke database.\nApakah ingin melakukan transaksi lagi?")
                        .setPositiveButton("Ya") { _, _ ->
                            with(binding) {
                                btnCount.visibility = View.VISIBLE
                                lblTotal.visibility = View.GONE
                                tvTotal.visibility = View.GONE
                                btnOrder.visibility = View.GONE
                                btnNewCustomer.visibility = View.VISIBLE

                                layoutCustomerID.isEndIconVisible = true
                                layoutMenu.isEndIconVisible = true
                                etStock.isEnabled = true

                                menuId = 0
                                custId = 0
                                total = 0.0
                                etCustomerID.setText("")
                                etStock.setText("")
                                etMenu.setText("")
                                etHarga.setText("0")
                                tvTotal.text = "Rp0,00"
                            }
                        }
                        .setNegativeButton("Tidak") { _, _ -> finish() }
                        .show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Data gagal untuk dikirim.")
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                }
            }

            val params = HashMap<String, String>()
            params["pembeli"] = custId.toString()
            params["id"] = menuId.toString()
            params["qty"] = binding.etStock.text.toString()

            val errorListener = Response.ErrorListener { error ->
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Error Code ${error.networkResponse.statusCode}.")
                    .setPositiveButton("OK") { _, _ -> }
                    .show()
            }

            val queue = Volley.newRequestQueue(this)
            val request = WebRequest(Request.Method.POST, MenuActivity.URL + "/transaksi", responseListener, errorListener, params)
            queue.add(request)
        }
    }

    fun checkRequirement() : Boolean {
        with(binding) {
            val map = mapOf(
                etCustomerID to layoutCustomerID,
                etMenu to layoutMenu,
                etStock to layoutStock,
            )

            map.forEach { (_, value) -> value.error = "" }
            val result = map.filter { (key, _) -> key.text.toString().isBlank() }

            if (binding.etStock.text.toString().toInt() > menu.stock) {
                layoutStock.error = "Stock tidak mencukupi"
                return false
            }

            return if (result.isNotEmpty()) {
                val error = "Wajib diisi!"
                result.forEach { (_, value) -> value.error = error}
                false
            } else true
        }
    }
}