package com.netlab.suhan.ui.customer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.netlab.suhan.R
import com.netlab.suhan.data.Customer
import com.netlab.suhan.databinding.ActivityCustomerBinding
import com.netlab.suhan.ui.MenuActivity
import com.netlab.suhan.ui.adapter.CustomerAdapter
import com.netlab.suhan.utils.WebRequest
import org.json.JSONArray
import org.json.JSONException

class CustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.list_pembeli)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchData()
    }

    private fun fetchData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvCustomer.visibility = View.GONE

        val responseListener = Response.Listener<String> { response ->
            val list = ArrayList<Customer>()
            try {
                val json = JSONArray(response)
                for (i: Int in 0 until json.length()) {
                    val jsonCustomer = json.getJSONObject(i)

                    val customer = Customer(
                        jsonCustomer.getInt("id_pembeli"),
                        jsonCustomer.getDouble("saldo"),
                        jsonCustomer.getDouble("pengeluaran")
                    )
                    list.add(customer)
                }

                val customerAdapter = CustomerAdapter()
                customerAdapter.setList(list)

                binding.progressBar.visibility = View.GONE
                binding.rvCustomer.visibility = View.VISIBLE

                with(binding.rvCustomer) {
                    layoutManager = LinearLayoutManager(this@CustomerActivity)
                    adapter = customerAdapter
                }
            } catch (e: JSONException) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Data gagal untuk dibaca.")
                    .setPositiveButton("OK") { _, _ -> finish() }
                    .show()
            } catch (e: IllegalArgumentException) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Terdapat data yang tidak sesuai dengan aplikasi ini.")
                    .setPositiveButton("OK") { _, _ -> finish() }
                    .show()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            val errorCode = error.networkResponse.statusCode

            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Error Code $errorCode.")
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }

        val queue = Volley.newRequestQueue(this)
        val request = WebRequest(Request.Method.GET, MenuActivity.URL + "/customer", responseListener, errorListener, HashMap())
        queue.add(request)
    }
}