package com.halilkaya.firebaseauthentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.firebaseauthentication.Fragments.YeniSohbetOdasiDialogFragment
import com.halilkaya.firebaseauthentication.Model.SohbetMesaj
import com.halilkaya.firebaseauthentication.Model.SohbetOdasi
import com.halilkaya.firebaseauthentication.adapter.SohbetOdasiRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_sohbet_odasi.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SohbetOdasiActivity : AppCompatActivity() {

    lateinit var tumSohbetOdalari:ArrayList<SohbetOdasi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_odasi)
        println("calismasi lazim")
        println("calismasi lazim")
        println("calismasi lazim")
        println("calismasi lazim")
        println("calismasi lazim")
        println("calismasi lazim")

        init()


    }

    fun init(){

        tumSohbetOdalariniGetir()


        fabtnYeniSohbetOdasi.setOnClickListener {
            var myYeniSohbetOdasi = YeniSohbetOdasiDialogFragment()
            myYeniSohbetOdasi.show(supportFragmentManager,"frag-YeniOda")
        }





    }

    fun tumSohbetOdalariniGetir(){
        println("buraya girdi")
        tumSohbetOdalari = ArrayList()

        var ref = FirebaseDatabase.getInstance().reference

        var sorgu = ref.child("sohbet_odasi").addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                println("?")
                Toast.makeText(this@SohbetOdasiActivity,"??",Toast.LENGTH_SHORT).show()

                for (tekSohbetOdasi in p0.children){

                    var tmpSohbetOdasi = SohbetOdasi()

                    var tmpMap = (tekSohbetOdasi.getValue() as HashMap<String , Object>)

                    tmpSohbetOdasi.sohbet_odasi_id = tmpMap.get("sohbet_odasi_id").toString()
                    tmpSohbetOdasi.sohbet_odasi_adi = tmpMap.get("sohbet_odasi_adi").toString()
                    tmpSohbetOdasi.seviye = tmpMap.get("seviye").toString()
                    tmpSohbetOdasi.olusturan_id = tmpMap.get("olusturan_id").toString()

                    var tumMesajlar = ArrayList<SohbetMesaj>()

                    for(tekMesaj in tekSohbetOdasi.child("sohbet_odasi_mesajlari").children){

                        var tmpMesaj = SohbetMesaj()

                        tmpMesaj.adi = tekMesaj.getValue(SohbetMesaj::class.java)?.adi
                        tmpMesaj.kullanici_id = tekMesaj.getValue(SohbetMesaj::class.java)?.kullanici_id
                        tmpMesaj.mesaj = tekMesaj.getValue(SohbetMesaj::class.java)?.mesaj
                        tmpMesaj.profil_resmi = tekMesaj.getValue(SohbetMesaj::class.java)?.profil_resmi
                        tmpMesaj.time = tekMesaj.getValue(SohbetMesaj::class.java)?.time

                        tumMesajlar.add(tmpMesaj)
                    }

                    tmpSohbetOdasi.sohbet_odasi_mesajlari = tumMesajlar
                    tumSohbetOdalari.add(tmpSohbetOdasi)



                }

                var adapter = SohbetOdasiRecyclerViewAdapter(tumSohbetOdalari)
                myRecyvlerView.adapter = adapter
                myRecyvlerView.layoutManager = LinearLayoutManager(this@SohbetOdasiActivity,LinearLayoutManager.VERTICAL,false)

            }


        })




    }

}
