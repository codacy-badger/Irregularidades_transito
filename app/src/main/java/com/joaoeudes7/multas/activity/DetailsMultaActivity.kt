package com.joaoeudes7.multas.activity

import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.joaoeudes7.multas.R
import com.joaoeudes7.multas.extraComponents.ProgressDialog.ProgressDialog
import com.joaoeudes7.multas.model.Multa
import kotlinx.android.synthetic.main.activity_deatils_multa.*
import java.text.SimpleDateFormat
import java.util.*

class DetailsMultaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var multa = Multa()
    private lateinit var keyOfMulta: String


    private lateinit var fbRealtimeDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deatils_multa)

        this.keyOfMulta = intent.extras.getString("key")

        // Map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapViewMulta) as SupportMapFragment
        mapFragment.getMapAsync(this@DetailsMultaActivity)
    }

    override fun onStart() {
        super.onStart()

        val progressDialog = ProgressDialog(this, "Aguarde...").show()

        this.fbRealtimeDB = FirebaseDatabase.getInstance().getReference("multas")
        this.fbRealtimeDB.child(keyOfMulta).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@DetailsMultaActivity, "Falha!", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    multa = dataSnapshot.getValue(Multa::class.java) as Multa
                    setDataInUI()
                    progressDialog.dismiss()
                }
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
    }

    fun setDataInUI() {
        val irregulariedades = Arrays.asList(
                "Farol desligado", "Transitar pelo aconstamento", "Pneus impróprios para uso",
                "Lâmpadas/luzes queimadas", "Sem uso dos acessórios", "Transporte de criança irregular",
                "Estacionado em espaço proibida")


        val portes = Arrays.asList("Alto", "Média", "Baixa")

        val local = Geocoder(this).getFromLocation(multa.local.latitude, multa.local.longitude, 1)[0]

        Glide.with(this).load(multa.urlThumbnail).into(imageView)
        txtEndereco.text = local.getAddressLine(0)
        txtPlaca.text = this.multa.carro.placa
        txtPorte.text = portes[this.multa.carro.porte]
        txtIrregularidade.text = irregulariedades[this.multa.irregulariedade]

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        txtDate.text = formatter.format(multa.data)

        this.setLocal()
    }

    fun setLocal() {
        val zoomLevel = 16f

        val myPlace = LatLng(this.multa.local.latitude, this.multa.local.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, zoomLevel))
        mMap.addMarker(MarkerOptions().position(myPlace).title("Local da multa"))

    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        mMap.uiSettings.isZoomControlsEnabled = true
    }

}
