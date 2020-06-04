package com.halilkaya.firebaseauthentication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.firebaseauthentication.Model.Kullanici
import com.halilkaya.firebaseauthentication.Model.SohbetMesaj
import com.halilkaya.firebaseauthentication.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tek_satir_mesaj.view.*
import java.util.zip.Inflater

class SohbetMesajRecyvlerViewAdapter(var context:Context,var mesajlar:ArrayList<SohbetMesaj>) : RecyclerView.Adapter<SohbetMesajRecyvlerViewAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var inflater = LayoutInflater.from(parent.context)

        var view:View? = null
        if(viewType == 1) {
            view = inflater.inflate(R.layout.tek_satir_mesaj2, parent, false)
        }else if(viewType == 2){
            view = inflater.inflate(R.layout.tek_satir_mesaj, parent, false)
        }


        return MyViewHolder(view!!)
    }

    override fun getItemViewType(position: Int): Int {

        if(mesajlar.get(position).kullanici_id == FirebaseAuth.getInstance().currentUser!!.uid){
            return 1
        }else{
            return 2
        }

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

            if(!oAnKiMesaj.kullanici_id.isNullOrEmpty()){

                var sorgu = FirebaseDatabase.getInstance().reference
                    .child("kullanici")
                    .orderByKey()
                    .equalTo(oAnKiMesaj.kullanici_id).addListenerForSingleValueEvent(object : ValueEventListener{

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            for(user in p0.children){
                                var resimUrl = user.getValue(Kullanici::class.java)?.profil_resmi
                                Picasso.get().load(resimUrl).resize(48,48).into(profilResmi)
                            }

                        }

                    })

            }


        }

    }



}