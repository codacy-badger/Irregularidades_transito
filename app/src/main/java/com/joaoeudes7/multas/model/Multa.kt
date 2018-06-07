package com.joaoeudes7.multas.model

import java.util.*

class Multa {
    var irregulariedade: Int = 0
    var data = Date()
    var carro = Carro()
    var local = LatLng()
    lateinit var urlThumbnail: String
}

class Carro {
    lateinit var placa: String
}

class LatLng {
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor()

    constructor(lat: Double, long: Double) {
        this.latitude = lat
        this.longitude = long
    }
}
