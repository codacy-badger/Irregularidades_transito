package com.joaoeudes7.multas.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Multa() : Parcelable {
    var irregulariedade: Int = 0
    var data = Date()
    var carro = Carro()
    var local = LatLng()
    lateinit var urlThumbnail: String

    constructor(parcel: Parcel) : this() {
        irregulariedade = parcel.readInt()
        urlThumbnail = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(irregulariedade)
        parcel.writeString(urlThumbnail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Multa> {
        override fun createFromParcel(parcel: Parcel): Multa {
            return Multa(parcel)
        }

        override fun newArray(size: Int): Array<Multa?> {
            return arrayOfNulls(size)
        }
    }

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
