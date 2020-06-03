package com.halilkaya.firebaseauthentication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.firebaseauthentication.Model.Kullanici
import com.halilkaya.firebaseauthentication.Model.SohbetOdasi
import com.halilkaya.firebaseauthentication.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tek_satir_sohbet_odasi.view.*
import java.util.zip.Inflater

class SohbetOdasiRecyclerViewAdapter(var tumSohbetOdalari:ArrayList<SohbetOdasi>) : RecyclerView.Adapter<SohbetOdasiRecyclerViewAdapter.MyViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var inflater = LayoutInflater.from(parent.context)

        var tek_satir = inflater.inflate(R.layout.tek_satir_sohbet_odasi,parent,false)

        return MyViewHolder(tek_satir)
    }

    override fun getItemCount(): Int {
        return tumSohbetOdalari.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var oAnKiSohbetOdasi = tumSohbetOdalari.get(position)

        holder.setData(oAnKiSohbetOdasi,position)


    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var tekSatir = itemView as CardView

        var imgOdayiOlusturaninResmi = tekSatir.imgOdayiOlusturaninResmi
        var tvOdayiOlusturaninAdi = tekSatir.tvOdayiOlusturaninAdi
        var btnSohbetiSil = tekSatir.btnSohbetiSil
        var tvOdaIsmi = tekSatir.tvOdaIsmi
        var tvMesajSayisi = tekSatir.tvMesajSayisi

        fun setData(oAnOlusturulanSohbetOdasi:SohbetOdasi,position:Int){

            tvOdaIsmi.setText(oAnOlusturulanSohbetOdasi.sohbet_odasi_adi)
            tvMesajSayisi.setText(oAnOlusturulanSohbetOdasi.sohbet_odasi_mesajlari?.size.toString())


            btnSohbetiSil.setOnClickListener {



            }



            var ref = FirebaseDatabase.getInstance().reference

            var query = ref.child("kullanici")
                .orderByKey()
                .equalTo(oAnOlusturulanSohbetOdasi.olusturan_id).addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        for(user in p0.children){

                            var resimPath = user.getValue(Kullanici::class.java)?.profil_resmi
                            Picasso.get().load(resimPath).into(imgOdayiOlusturaninResmi)
                            tvOdayiOlusturaninAdi.setText(user.getValue(Kullanici::class.java)?.isim)

                        }

                    }


                })




        }

    }

}