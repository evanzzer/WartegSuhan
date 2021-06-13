package com.netlab.suhan.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.netlab.suhan.R
import com.netlab.suhan.databinding.ActivityTopUpBinding
import com.netlab.suhan.ui.customer.DialogCustomer
import com.netlab.suhan.utils.WebRequest

class TopUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTopUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.top_up)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rgMethod.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbUpdate.id)
                binding.layoutCustomerID.visibility = View.VISIBLE
            else binding.layoutCustomerID.visibility = View.GONE
        }

        binding.layoutCustomerID.setEndIconOnClickListener {
            val dialog = DialogCustomer()
            dialog.onDataSelected = { data ->
                binding.etCustomerID.setText(data.id.toString())
            }
            dialog.show(supportFragmentManager, null)
        }

        binding.btnTopUp.setOnClickListener {
            val value = binding.etJumlah.text.toString().replace(".", "")
            with(binding) {
                etCustomerID.isEnabled = false
                etJumlah.isEnabled = false
            }

            if (value.toInt() > 0) {
                val id = binding.etCustomerID.text.toString()

                val responseListener = Response.Listener<String> { response ->
                    if (response != null && response.isNotBlank()) {
                        AlertDialog.Builder(this)
                            .setTitle("Informasi")
                            .setMessage("Data berhasil ditambahkan ke database.\nApakah ingin melakukan top-up lagi?")
                            .setPositiveButton("Ya") { _, _ ->
                                binding.etCustomerID.setText("")
                                binding.etJumlah.setText("0")
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

                val errorListener = Response.ErrorListener { error ->
                    val errorCode = error.networkResponse.statusCode
                    if (errorCode == 404) {
                        AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Data untuk pembeli $id tidak dapat ditemukan.")
                            .setPositiveButton("OK") { _, _ -> }
                            .show()
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Error Code $errorCode.")
                            .setPositiveButton("OK") { _, _ -> }
                            .show()
                    }
                }

                val queue = Volley.newRequestQueue(this)
                val params = HashMap<String, String>()
                params["saldo"] = value

                val method = if (binding.rgMethod.checkedRadioButtonId == binding.rbUpdate.id) {
                    params["id"] = id
                    Request.Method.PUT
                } else {
                    Request.Method.POST
                }

                val request = WebRequest(method, MenuActivity.URL + "/topup", responseListener, errorListener, params)
                queue.add(request)
            }

            with(binding) {
                etCustomerID.isEnabled = true
                etJumlah.isEnabled = true
            }
        }
    }
}