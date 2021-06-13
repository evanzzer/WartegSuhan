package com.netlab.suhan.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.netlab.suhan.R
import com.netlab.suhan.data.Order
import com.netlab.suhan.databinding.ActivityTransactionBinding
import com.netlab.suhan.ui.adapter.TransactionAdapter
import com.netlab.suhan.utils.WebRequest
import org.json.JSONArray
import org.json.JSONException

class TransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.list_pembeli)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchData()
    }

    private fun fetchData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvTransaction.visibility = View.GONE

        val responseListener = Response.Listener<String> { response ->
            val list = ArrayList<Order>()
            try {
                val json = JSONArray(response)
                for (i: Int in 0 until json.length()) {
                    val jsonTransaction = json.getJSONObject(i)

                    val transaction = Order(
                        jsonTransaction.getInt("id_order"),
                        jsonTransaction.getInt("id_pembeli"),
                        jsonTransaction.getString("nama_menu"),
                        jsonTransaction.getInt("jumlah_stok"),
                        jsonTransaction.getDouble("total_harga")
                    )
                    list.add(transaction)
                }

                val transactionAdapter = TransactionAdapter()
                transactionAdapter.setList(list)

                binding.progressBar.visibility = View.GONE
                binding.rvTransaction.visibility = View.VISIBLE

                with(binding.rvTransaction) {
                    layoutManager = LinearLayoutManager(this@TransactionActivity)
                    adapter = transactionAdapter
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
        val request = WebRequest(Request.Method.GET, MenuActivity.URL + "/transaksi", responseListener, errorListener, HashMap())
        queue.add(request)
    }
}