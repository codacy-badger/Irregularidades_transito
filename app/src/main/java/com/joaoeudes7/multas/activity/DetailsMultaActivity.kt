package com.joaoeudes7.multas.activity

import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.joaoeudes7.multas.R
import com.joaoeudes7.multas.model.Multa
import kotlinx.android.synthetic.main.activity_deatils_multa.*
import java.text.SimpleDateFormat
import java.util.*

class DetailsMultaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var multa = Multa()

    private val _typeMulta = hashMapOf(
            0 to "Estacionado em vaga de Deficiente",
            1 to "Estacionado em vaga de Idosos"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deatils_multa)

        this.multa = intent.extras.getParcelable("multa")

        try {
            val local = Geocoder(this).getFromLocation(multa.local.latitude, multa.local.longitude, 1)[0]

            Glide.with(this).load(multa.urlThumbnail).into(imageView)
            txtEndereco.text = local.getAddressLine(0)
            txtPlaca.text = this.multa.carro.placa
            txtIrregularidade.text = this._typeMulta[this.multa.irregulariedade]

            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            txtDate.text = formatter.format(multa.data)
        } catch (error: Error) {
            println(error.message)
        }

    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        val zoomLevel = 20f

        mMap.uiSettings.isZoomControlsEnabled = true

        // Add a marker in Sydney and move the camera
        val myPlace = LatLng(this.multa.local.latitude, this.multa.local.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, zoomLevel))
        mMap.addMarker(MarkerOptions().position(myPlace).title("Local da multa"))
    }

}
