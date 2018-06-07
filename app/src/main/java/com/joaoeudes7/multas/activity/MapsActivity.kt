package com.joaoeudes7.multas.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.joaoeudes7.multas.R

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    lateinit var mMap: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapViewMulta) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        // Add a marker in Sydney and move the camera
        val myPlace = LatLng(-6.5938807, -36.7769323)  // this is New York

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12.0f))

        mMap.isMyLocationEnabled = true

        this.fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                println("My last location is: ${location}")
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("Local da multa"))
            }
        }

    }

    override fun onMarkerClick(p0: Marker?) = false
}
