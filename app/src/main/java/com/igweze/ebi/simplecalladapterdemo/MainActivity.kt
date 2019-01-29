package com.igweze.ebi.simplecalladapterdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter = UserAdapter()
    private val service by lazy { Container.getServiceInstance() }
    private var resultCount = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup ui
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        service.getUsers(resultCount).process(::handleResponse).bind(this)
    }

    private fun setupUI() {
        btnGetResult.setOnClickListener {
            val str = etResultCount.text.toString()
            if (str.isNullOrEmpty()) return@setOnClickListener

            resultCount = str.toInt()
            service.getUsers(resultCount).process(::handleResponse)//.bind(this)
        }

        // setup recycler view
        rvUsers.adapter = adapter
        val layoutManager =  LinearLayoutManager(this)
        rvUsers.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        rvUsers.layoutManager = layoutManager
    }

    private fun handleResponse(response: Response<List<Result>>?, throwable: Throwable?) {
        runOnUiThread {
            if (throwable != null) {
                Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (response == null) Toast.makeText(this, "No result returned", Toast.LENGTH_LONG).show()
                else adapter.setUsers(response.results)
            }
        }
    }

}
