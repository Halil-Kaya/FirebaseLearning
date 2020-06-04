package com.halilkaya.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.halilkaya.firebaseauthentication.Model.Kullanici
import com.halilkaya.firebaseauthentication.Model.SohbetMesaj
import com.halilkaya.firebaseauthentication.adapter.SohbetMesajRecyvlerViewAdapter
import kotlinx.android.synthetic.main.activity_mesajlasma_activiyu.*

class MesajlasmaActiviyu : AppCompatActivity() {

    var tumMesajlar:ArrayList<SohbetMesaj>? = null

    lateinit var myReference:DatabaseReference

    var myAuthStateListener:FirebaseAuth.AuthStateListener? = null
    var sohbetOdasiId:String? = ""

    var myAdapter:SohbetMesajRecyvlerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesajlasma_activiyu)
        baslatFirebaseAuthStateListener()

        sohbetOdasiOgren()

    }


    fun sohbetOdasiOgren(){
        sohbetOdasiId = intent.getStringExtra("sohbetID")
        baslatMesajListener()


    }

    var myValueEventListener = object : ValueEventListener{

        override fun onCancelled(p0: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onDataChange(p0: DataSnapshot) {

            sohbetOdasindakiMesajlariGetir()

        }

    }

    fun sohbetOdasindakiMesajlariGetir(){

        if(tumMesajlar == null){
            tumMesajlar = ArrayList()
        }

        var sorgu = myReference.addValueEventListener(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for(sohbetMesaj in p0.children){

                    var tmpSohbetMesaj = SohbetMesaj()

                    var kullanici_id = sohbetMesaj.getValue(SohbetMesaj::class.java)?.kullanici_id

                    if(kullanici_id != null){

                        tmpSohbetMesaj.mesaj = sohbetMesaj.getValue(SohbetMesaj::class.java)?.mesaj
                        tmpSohbetMesaj.time = sohbetMesaj.getValue(SohbetMesaj::class.java)?.time
                        tmpSohbetMesaj.kullanici_id = sohbetMesaj.getValue(SohbetMesaj::class.java)?.kullanici_id

                        var kullaniciDetaylari = myReference.child("kullanici")
                            .orderByKey()
                            .equalTo(kullanici_id).addListenerForSingleValueEvent(object : ValueEventListener{

                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {

                                    for(user in p0.children){
                                        tmpSohbetMesaj.profil_resmi = user.getValue(Kullanici::class.java)?.profil_resmi
                                        tmpSohbetMesaj.adi = user.getValue(Kullanici::class.java)?.isim
                                    }


                                }


                            })


                    }else{

                        tmpSohbetMesaj.mesaj = sohbetMesaj.getValue(SohbetMesaj::class.java)?.mesaj
                        tmpSohbetMesaj.time = sohbetMesaj.getValue(SohbetMesaj::class.java)?.time
                        tmpSohbetMesaj.profil_resmi = ""
                        tmpSohbetMesaj.adi = ""



                    }

                    tumMesajlar!!.add(tmpSohbetMesaj)
                    myAdapter?.notifyDataSetChanged()

                }


            }

        })


        if(myAdapter == null){
            initMesajlarListesi()
        }




    }

    fun initMesajlarListesi(){

         myAdapter = SohbetMesajRecyvlerViewAdapter(this,tumMesajlar!!)
        rvMesajlar.adapter = myAdapter
        rvMesajlar.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvMesajlar.scrollToPosition(myAdapter!!.itemCount -1)

    }



    fun baslatMesajListener(){
        myReference = FirebaseDatabase.getInstance().reference
                .child("sohbet_odasi")
                .child(sohbetOdasiId+"")
                .child("sohbet_odasi_mesajlari")

        myReference.addValueEventListener(myValueEventListener)

    }


    fun baslatFirebaseAuthStateListener(){

        myAuthStateListener = object : FirebaseAuth.AuthStateListener{

            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kullanici = p0.currentUser

                if(kullanici == null){

                    var intent = Intent(this@MesajlasmaActiviyu,LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }

            }

        }

    }


    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListener!!)
    }


    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(myAuthStateListener!!)
    }


    override fun onResume() {
        super.onResume()
        kullaniciKontrolEt()
    }

    fun kullaniciKontrolEt(){

        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici == null){
            var intent = Intent(this@MesajlasmaActiviyu,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    /*
    fun mesajlariGetir(){
        var sohbetOdasiID = intent.getStringExtra("sohbetID")
        var ref = FirebaseDatabase.getInstance().reference
        var sorgu = ref.child("sohbet_odasi")
            .child(sohbetOdasiID)
            .child("sohbet_odasi_mesajlari")
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onCancelled(p0: DatabaseError) {

                }
                override fun onDataChange(p0: DataSnapshot) {
                    for(sohbetMesaj in p0.children){
                        tumMesajlar.add(sohbetMesaj.getValue(SohbetMesaj::class.java) as SohbetMesaj)
                    }
                    Toast.makeText(this@MesajlasmaActiviyu,"size: ${tumMesajlar.size}",Toast.LENGTH_LONG).show()
                }
            })
    }
     */
}