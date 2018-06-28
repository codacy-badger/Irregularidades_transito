package com.joaoeudes7.multas.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.joaoeudes7.multas.R
import com.joaoeudes7.multas.activity.DetailsMultaActivity
import com.joaoeudes7.multas.model.Multa
import kotlinx.android.synthetic.main.row_multa.view.*
import java.util.*


class MultaAdapter(var multas: ArrayList<Multa>) : RecyclerView.Adapter<MultaAdapter.CustomPostViewHolder>() {
    private var context: Context? = null

    private val irregulariedades = Arrays.asList(
            "Farol desligado", "Transitar pelo aconstamento", "Pneus impróprios para uso",
            "Lâmpadas/luzes queimadas", "Sem uso dos acessórios", "Transporte de criança irregular",
            "Estacionado em espaço proibida")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomPostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.row_multa, parent, false)
        this.context = parent.context
        return CustomPostViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        return multas.size
    }

    override fun onBindViewHolder(holder: CustomPostViewHolder, position: Int) {
        val multa = multas[position]
        val local = Geocoder(context).getFromLocation(multa.local.latitude, multa.local.longitude, 1)[0]

        Glide.with(holder.view).load(multa.urlThumbnail).into(holder.imageView)
        holder.title.text = irregulariedades[multa.irregulariedade]
        holder.description.text = local.getAddressLine(0)

        holder.card.setOnClickListener { goToDetails(context!!, multa) }
    }


    private fun goToDetails(context: Context, multa: Multa) {
        val intentDetails = Intent(context, DetailsMultaActivity::class.java)
        intentDetails.putExtra("key", multa.key)
        (this.context as Activity).startActivityForResult(intentDetails, 1)
        (this.context as Activity).overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
    }


    class CustomPostViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val card = view.constraintLayout!!
        val imageView = view.thumbnail!!
        var title = view.title!!
        var description = view.description!!
    }

}