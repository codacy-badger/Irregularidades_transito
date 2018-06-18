package com.joaoeudes7.multas.model

class Local {
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor()

    constructor(lat: Double, long: Double) {
        this.latitude = lat
        this.longitude = long
    }
}