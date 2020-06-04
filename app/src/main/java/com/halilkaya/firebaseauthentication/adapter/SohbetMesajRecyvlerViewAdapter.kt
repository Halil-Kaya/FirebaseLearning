package com.halilkaya.firebaseauthentication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.halilkaya.firebaseauthentication.Model.SohbetMesaj
import com.halilkaya.firebaseauthentication.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tek_satir_mesaj.view.*
import java.util.zip.Inflater

class SohbetMesajRecyvlerViewAdapter(var context:Context,var mesajlar:ArrayList<SohbetMesaj>) : RecyclerView.Adapter<SohbetMesajRecyvlerViewAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var view = inflater.inflate(R.layout.tek_satir_mesaj,parent,false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mesajlar.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var oAnkiMesaj = mesajlar.get(position)
        holder.setData(oAnkiMesaj,position)

    }




    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var tek_satir = itemView as CardView
        var profilResmi = tek_satir.imgProfilResmi
        var mesaj = tek_satir.tvMesaj
        var isim = tek_satir.tvYazarAdi
        var tarih = tek_satir.tvTarih



        fun setData(oAnKiMesaj : SohbetMesaj , position:Int){

            mesaj.setText(oAnKiMesaj.mesaj)
            isim.setText(oAnKiMesaj.adi)
            tarih.setText(oAnKiMesaj.time)

            if(!oAnKiMesaj.profil_resmi.isNullOrEmpty()){
                Picasso.get().load(oAnKiMesaj.profil_resmi).resize(48,48).into(profilResmi)
            }

        }

    }



}