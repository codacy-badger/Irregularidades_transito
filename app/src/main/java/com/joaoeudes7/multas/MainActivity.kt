package com.joaoeudes7.multas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.*
import com.joaoeudes7.multas.activity.CreateMultaActivity
import com.joaoeudes7.multas.activity.LoginActivity
import com.joaoeudes7.multas.adapters.MultaAdapter
import com.joaoeudes7.multas.model.Multa
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    val multas: MutableList<Multa> = mutableListOf()
    lateinit var fbRealtimeDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Set Recycle Layout
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        recyclerView.isDrawingCacheEnabled = true

        // Buttons and Actions
        fab.setOnClickListener { goToNewMulta() }
    }

    override fun onStart() {
        super.onStart()

        // Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions()
        }

        this.getData()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getData() {
        fbRealtimeDB = FirebaseDatabase.getInstance().getReference("multas")
        fbRealtimeDB.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MainActivity, "sorry", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    multas.clear()
                    dataSnapshot.children.mapIndexedNotNullTo(multas) { index, dataSnapshot -> dataSnapshot.getValue<Multa>(Multa::class.java) }
                    recyclerView.adapter = MultaAdapter(multas)
                }
            }

        })
    }

    private fun goToLogin() {
        val login = Intent(this@MainActivity, LoginActivity::class.java)
        return startActivity(login)
    }

    private fun goToNewMulta() {
        val login = Intent(this@MainActivity, CreateMultaActivity::class.java)
        return startActivity(login)
    }

    private fun requestPermissions() {
        val PERMISSIONS = arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA)

        if (!hasPermissions(PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}
