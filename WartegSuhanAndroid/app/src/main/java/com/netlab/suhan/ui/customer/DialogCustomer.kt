package com.netlab.suhan.ui.customer

import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.netlab.suhan.R
import com.netlab.suhan.data.Customer
import com.netlab.suhan.databinding.DialogMenuBinding
import com.netlab.suhan.ui.MenuActivity
import com.netlab.suhan.ui.adapter.CustomerAdapter
import com.netlab.suhan.utils.WebRequest
import org.json.JSONArray
import org.json.JSONException

class DialogCustomer : DialogFragment() {
    var onDataSelected: ((Customer) -> Unit)? = null

    private var _binding: DialogMenuBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()

        if (dialog == null) return

        val rect = Rect()
        val width = if (resources.getBoolean(R.bool.tablet)) {
            dialog!!.window?.decorView?.getWindowVisibleDisplayFrame(rect)
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                (0.5 * rect.width()).toInt()
            else (0.7 * rect.width()).toInt()
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            (0.5 * rect.width()).toInt()
        } else WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog!!.window?.setLayout(width, height)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.setCanceledOnTouchOutside(false)

        showLoading(true)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

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

                showLoading(false)

                with(binding.rvMenu) {
                    layoutManager = LinearLayoutManager(dialog!!.context)
                    adapter = customerAdapter
                }

                customerAdapter.onDataSelected = { item ->
                    onDataSelected?.invoke(item)
                    dismiss()
                }
            } catch (e: JSONException) {
                AlertDialog.Builder(dialog!!.context)
                    .setTitle("Error")
                    .setMessage("Data gagal untuk dibaca.")
                    .setPositiveButton("OK") { _, _ -> dismiss() }
                    .show()
            } catch (e: IllegalArgumentException) {
                AlertDialog.Builder(dialog!!.context)
                    .setTitle("Error")
                    .setMessage("Terdapat data yang tidak sesuai dengan aplikasi ini.")
                    .setPositiveButton("OK") { _, _ -> dismiss() }
                    .show()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            val errorCode = error.networkResponse.statusCode

            AlertDialog.Builder(dialog!!.context)
                .setTitle("Error")
                .setMessage("Error Code $errorCode.")
                .setPositiveButton("OK") { _, _ -> dismiss() }
                .show()
        }

        val queue = Volley.newRequestQueue(dialog!!.context)
        val request = WebRequest(Request.Method.GET, MenuActivity.URL + "/customer", responseListener, errorListener, HashMap())
        queue.add(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(state: Boolean) {
        with(binding) {
            progressBar.visibility = if (state) View.VISIBLE else View.GONE
            rvMenu.visibility = if (state) View.GONE else View.VISIBLE
        }
    }
}