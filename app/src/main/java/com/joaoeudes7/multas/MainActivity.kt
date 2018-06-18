package com.joaoeudes7.multas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.*
import com.joaoeudes7.multas.activity.CreateMultaActivity
import com.joaoeudes7.multas.adapters.MultaAdapter
import com.joaoeudes7.multas.extraComponents.ProgressDialog.ProgressDialog
import com.joaoeudes7.multas.model.Multa
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_app_bar.*
import kotlinx.android.synthetic.main.main_content.*
import org.jetbrains.anko.alert


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val multas: ArrayList<Multa> = ArrayList()
    private lateinit var fbRealtimeDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        // Set Recycle Layout
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        recyclerView.isDrawingCacheEnabled = true

        // Buttons and Actions
        btn_new_multa.setOnClickListener { goToNewMulta() }
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
        val progressBar = ProgressDialog(this)
        progressBar.show()

        fbRealtimeDB = FirebaseDatabase.getInstance().getReference("multas")
        fbRealtimeDB.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MainActivity, "sorry", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    multas.clear()
                    dataSnapshot.children.mapIndexedNotNullTo(multas) { _, data -> data.getValue(Multa::class.java) as Multa }
                    if (multas.size > 0) {
                        recyclerView.adapter = MultaAdapter(multas)
                    }
                    progressBar.dismiss()
                } else {
                    Toast.makeText(this@MainActivity, "Falha em receber os dados",Toast.LENGTH_LONG).show()
                    progressBar.dismiss()
                }
            }

        })
    }

    private fun goToNewMulta() {
        val login = Intent(this@MainActivity, CreateMultaActivity::class.java)
        startActivity(login)
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA)

        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 1)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_about -> {
                alert {
                    message = "by joaoeudes7\nEm breve mais novidades!"
                }.show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
