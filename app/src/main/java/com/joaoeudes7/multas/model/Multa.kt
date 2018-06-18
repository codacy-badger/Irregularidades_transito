package com.joaoeudes7.multas.model

import java.util.*

class Multa {
    lateinit var key: String
    var irregulariedade: Int = 0
    var data = Date()
    var carro = Carro()
    var local = Local()
    lateinit var urlThumbnail: String
}


