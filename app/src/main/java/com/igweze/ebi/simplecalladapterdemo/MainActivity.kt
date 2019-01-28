package com.igweze.ebi.simplecalladapterdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter = UserAdapter()
    private var resultCount = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup ui
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        val service = Container.getServiceInstance()
        service.getUsers(resultCount).process(::handleResponse).bind(this)
    }

    private fun setupUI() {

        // setup recycler view
        rvUsers.adapter = adapter
        rvUsers.layoutManager = LinearLayoutManager(this)
    }

    private fun handleResponse(response: Response<List<User>>?, throwable: Throwable?) {
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
