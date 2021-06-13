package com.netlab.suhan.ui.stock

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.netlab.suhan.R
import com.netlab.suhan.data.Menu
import com.netlab.suhan.data.MenuType
import com.netlab.suhan.databinding.ActivityStockBinding
import com.netlab.suhan.ui.MenuActivity
import com.netlab.suhan.ui.adapter.StockAdapter
import com.netlab.suhan.utils.WebRequest
import org.json.JSONArray
import org.json.JSONException

class StockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStockBinding

    companion object {
        private const val REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.list_stock)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchData()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@StockActivity, EditStockActivity::class.java)
            intent.putExtra(EditStockActivity.EXTRA_EDIT_MODE, false)
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == EditStockActivity.RESULT_CODE) {
            fetchData()
        }
    }

    private fun fetchData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvStockList.visibility = View.GONE
        binding.fabAdd.visibility = View.GONE

        val responseListener = Response.Listener<String> { response ->
            val list = ArrayList<Menu>()
            try {
                val json = JSONArray(response)
                for (i: Int in 0 until json.length()) {
                    val jsonMenu = json.getJSONObject(i)

                    val menu = Menu(
                        jsonMenu.getInt("id_menu"),
                        MenuType.valueOf(jsonMenu.getString("jenis_menu")),
                        jsonMenu.getString("nama_menu"),
                        jsonMenu.getInt("jumlah_stok"),
                        jsonMenu.getDouble("harga")
                    )
                    list.add(menu)
                }

                val stockAdapter = StockAdapter()
                stockAdapter.setList(list)

                binding.progressBar.visibility = View.GONE
                binding.rvStockList.visibility = View.VISIBLE
                binding.fabAdd.visibility = View.VISIBLE

                with(binding.rvStockList) {
                    layoutManager = LinearLayoutManager(this@StockActivity)
                    adapter = stockAdapter
                }

                stockAdapter.onDataSelected = { item ->
                    val intent = Intent(this@StockActivity, EditStockActivity::class.java)
                    intent.putExtra(EditStockActivity.EXTRA_STOCK_ACTIVITY, item)
                    intent.putExtra(EditStockActivity.EXTRA_EDIT_MODE, true)
                    startActivityForResult(intent, 100)
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
        val request = WebRequest(Request.Method.GET, MenuActivity.URL + "/menu", responseListener, errorListener, HashMap())
        queue.add(request)
    }
}