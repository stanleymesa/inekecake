package com.example.inekecake.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.inekecake.API.APIRequestData
import com.example.inekecake.API.RetroServer
import com.example.inekecake.Adapter.CustomerAdapter
import com.example.inekecake.Model.DataModel
import com.example.inekecake.Model.ResponseModel
import com.example.inekecake.R
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class CustomersActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var rvCustomers: RecyclerView
    private lateinit var lmCustomers: RecyclerView.LayoutManager
    private lateinit var listCustomers: ArrayList<DataModel>
    private lateinit var srlData: SwipeRefreshLayout
    private lateinit var pbData: ProgressBar
    private lateinit var fabData: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_customers)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Data Customers"

        // Set Recyclerview
        rvCustomers = findViewById(R.id.rv_customers)
        lmCustomers = LinearLayoutManager(this)
        rvCustomers.layoutManager = lmCustomers

        // Set SRL dan PB
        srlData = findViewById(R.id.srl_data)
        pbData = findViewById(R.id.pb_data)

        // Set FAB
        fabData = findViewById(R.id.fab_data)
        fabData.setOnClickListener(this)


        srlData.setOnRefreshListener {
            retrieveData()
        }

    }

    override fun onResume() {
        super.onResume()
        retrieveData()
    }

    private fun retrieveData() {
        // srl dan pb refreshing
        srlData.isRefreshing = true
        pbData.isVisible = true

        val ardData: APIRequestData = RetroServer.konekRetrofit().create(APIRequestData::class.java)
        val tampilData: Call<ResponseModel> = ardData.ardRetrieveData()

        tampilData.enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                val message: String = response.body()?.message ?: "kosong"
                listCustomers = response.body()?.data ?: arrayListOf()

                // set adapter
                rvCustomers.adapter = CustomerAdapter(listCustomers)

                // set srl dan pb hilang
                srlData.isRefreshing = false
                pbData.isVisible = false

            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Toast.makeText(this@CustomersActivity, "Gagal Menghubungi Server, ${t.message}", Toast.LENGTH_LONG).show()

                // set srl dan pb hilang
                srlData.isRefreshing = false
                pbData.isVisible = false
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_data -> {
                val intent = Intent(this, CreateDataActivity::class.java)
                startActivity(intent)
            }
        }
    }
}