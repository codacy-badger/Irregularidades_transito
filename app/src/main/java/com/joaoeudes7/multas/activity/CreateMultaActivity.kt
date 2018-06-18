package com.joaoeudes7.multas.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.joaoeudes7.multas.R
import com.joaoeudes7.multas.extraComponents.ProgressDialog.ProgressDialog
import com.joaoeudes7.multas.model.Multa
import kotlinx.android.synthetic.main.activity_create_multa.*
import kotlinx.android.synthetic.main.content_create_multa.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import java.util.regex.Pattern

class CreateMultaActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var multa = Multa()

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var fbStorage: StorageReference
    private lateinit var fbRealtimeDB: DatabaseReference

    private val PICK_IMAGE_REQUEST = 7
    private val TAKE_IMAGE_REQUEST = 77

    private lateinit var imageUri: Uri
    private lateinit var imageByteArray: ByteArray

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_multa)

        // Map
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapViewMulta) as SupportMapFragment
        mapFragment.getMapAsync(this@CreateMultaActivity)

        // Spinner
        val inrregulariedades = Arrays.asList(
                "Farol desligado", "Transitar pelo aconstamento", "Pneus impróprios para uso",
                "Lâmpadas/luzes queimadas", "Sem uso dos acessórios", "Transporte de criança inrregular",
                "Estacionado em vaga proibida")
        spinner_tipo_multa.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, inrregulariedades)
        spinner_tipo_multa.prompt = "Selecione a inrregulariedade"

        val portes = Arrays.asList("Alto", "Média", "Baixa")
        spinner_porte.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, portes)
        spinner_porte.prompt = "Selecione o porte do veículo"

        // Buttons and Actions
        btn_camera.setOnClickListener { launchCamera() }
        btn_gallery.setOnClickListener { selectByGallery() }
        btn_submit.setOnClickListener {
            if (isPlaceValid() && isImageValid()) { onSubmit() }
        }

    }

    override fun onStart() {
        super.onStart()

        this.fbRealtimeDB = FirebaseDatabase.getInstance().getReference("multas")
        this.fbStorage = FirebaseStorage.getInstance().getReference("multas")

        this.progressDialog = ProgressDialog(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data?.data != null) {
            this.imageUri = data.data
            imageView.setImageURI(imageUri)
            doAsync {
                val byteArray = compressImage(imageUri)

                uiThread {
                    imageByteArray = byteArray
                }
            }
        }

        if (requestCode == TAKE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageView.setImageURI(imageUri)
            doAsync {
                val byteArray = compressImage(imageUri)

                uiThread {
                    imageByteArray = byteArray
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
    }

    private fun launchCamera() {
        val path = File(this.filesDir, "Pictures")
        if (!path.exists()) path.mkdirs()
        val createImage = File(path, "image.jpg")

        imageUri = FileProvider.getUriForFile(this, "com.joaoeudes7.multas.provider", createImage)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, TAKE_IMAGE_REQUEST)
    }


    private fun selectByGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun compressImage(imageURI: Uri): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageURI)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 50, stream)
        val byteArray = stream.toByteArray()
        stream.close()
        return byteArray
    }

    private fun onSubmit() {
        this.progressDialog.show()

        this.multa.carro.placa = edt_placa.text.toString()
        this.multa.irregulariedade = spinner_tipo_multa.selectedItemPosition
        this.multa.carro.porte = spinner_porte.selectedItemPosition


        // Firebase
        this.sendImageToStorage(imageByteArray)
    }

    private fun sendImageToStorage(imageBytes: ByteArray) {
        fbStorage.child(UUID.randomUUID().toString()).putBytes(imageBytes).
                addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                this.multa.urlThumbnail = it.toString()

                this.sendMulta()
            }
        }
    }

    private fun sendMulta() {
        val post = fbRealtimeDB.push()
        this.multa.key = post.key!!
        post.setValue(multa)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Enviado com suncesso!", Toast.LENGTH_LONG).show()
                    finish()
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Falha no envio!", Toast.LENGTH_LONG).show()
                }
    }

    // Maps SDK
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        val zoomLevel = 16f

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        // Add a marker in Sydney and move the camera
        val myPlace = LatLng(-6.5938807, -36.7769323)  // First location

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, zoomLevel))

        mMap.isMyLocationEnabled = true

        this.fusedLocationClient.lastLocation
                .addOnSuccessListener(this) { location ->
                    if (location != null) {
                        println("My last location is: $location")
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel))
                        mMap.addMarker(MarkerOptions().position(currentLatLng).title("Local da multa"))
                        multa.local = com.joaoeudes7.multas.model.Local(currentLatLng.latitude, currentLatLng.longitude)
                    }
                }
    }

    override fun onMarkerClick(p0: Marker?) = false


    // Validators
    private fun isPlaceValid(): Boolean {
        val isValid = Pattern.compile("^\\w{3}-\\d{4}\$").matcher(edt_placa.text.toString()).matches()
        if (!isValid) {
            alert {
                message = "Placa inválida!"
                positiveButton("Ok") {}
            }.show()
        }

        return isValid
    }


    private fun isImageValid(): Boolean {
        val isValid = ::imageUri.isInitialized
        if (!isValid) {
            alert {
                message = "Adicione uma imagem"
                positiveButton("Ok") {}
            }.show()
        }

        return isValid
    }
}
