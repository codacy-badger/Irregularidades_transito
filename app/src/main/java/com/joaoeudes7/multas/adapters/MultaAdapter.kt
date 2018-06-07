package com.joaoeudes7.multas.adapters

import android.content.Context
import android.location.Geocoder
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.joaoeudes7.multas.R
import com.joaoeudes7.multas.model.Multa
import kotlinx.android.synthetic.main.row_multa.view.*
import java.io.IOException


class MultaAdapter(var multas: MutableList<Multa>) : RecyclerView.Adapter<CustomPostViewHolder>() {
    var context: Context? = null

    private val _typeMulta = hashMapOf(
            0 to "Estacionado em vaga de Deficiente",
            1 to "Estacionado em vaga de Idosos"
    )


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

        try {
            val _postActual = multas[position]

            val local = Geocoder(context).getFromLocation(_postActual.local.latitude, _postActual.local.longitude, 1)[0]
//            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            Glide.with(holder.view).load(_postActual.urlThumbnail).into(holder.imageView)
            holder.title.text = _typeMulta.get(_postActual.irregulariedade)
            holder.description.text = local.getAddressLine(0)
//            holder.date.text = formatter.format(_postActual.data)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

class CustomPostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val imageView = view.thumbnail!!
    var title = view.title
    var description = view.description
//    var date = view.date
}