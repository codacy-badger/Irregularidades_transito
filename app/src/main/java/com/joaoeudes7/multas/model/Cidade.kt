package com.joaoeudes7.multas.model

class Cidade {
    val cep: String = ""
    val nome: String = ""
    var multas: ArrayList<Multa> = ArrayList()

    fun addMulta(multa: Multa) {
        this.multas.add(multa)
    }
}

