package com.halilkaya.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.halilkaya.firebaseauthentication.Model.FCMModel
import com.halilkaya.firebaseauthentication.Model.Kullanici
import com.halilkaya.firebaseauthentication.Model.SohbetMesaj
import com.halilkaya.firebaseauthentication.adapter.SohbetMesajRecyvlerViewAdapter
import com.halilkaya.firebaseauthentication.services.FCMInterface
import kotlinx.android.synthetic.main.activity_mesajlasma_activiyu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class MesajlasmaActiviyu : AppCompatActivity() {

    var tumMesajlar:ArrayList<SohbetMesaj>? = null

    lateinit var myReference:DatabaseReference

    val BASE_URL = "https://fcm.googleapis.com/fcm/"

    lateinit var SERVER_KEY:String


    var myAuthStateListener:FirebaseAuth.AuthStateListener? = null
    var sohbetOdasiId:String? = ""
    var myHashSet:HashSet<String>? = null

    var myAdapter:SohbetMesajRecyvlerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesajlasma_activiyu)
        baslatFirebaseAuthStateListener()
        initServerKeyOku()
        init()
        sohbetOdasiOgren()

    }
    fun initServerKeyOku(){

        var ref = FirebaseDatabase.getInstance().reference
            .child("server")
            .orderByValue()
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var singleSnapshot = p0.children.iterator().next()
                    SERVER_KEY = singleSnapshot.getValue().toString()
                    println("server_key : ${SERVER_KEY}")
                }

            })


    }


    fun init(){

        btnMesajGonder.setOnClickListener {

            if(etMesaj.text.isNotEmpty()){

                var sohbetMesaj = SohbetMesaj()
                sohbetMesaj.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
                sohbetMesaj.time = getTarih()
                sohbetMesaj.mesaj = etMesaj.text.toString()


                var ref = FirebaseDatabase.getInstance().reference.child("sohbet_odasi")
                        .child(sohbetOdasiId.toString())
                        .child("sohbet_odasi_mesajlari")

                var yeniMesajID = ref.push().key

                ref.child(yeniMesajID+"")
                    .setValue(sohbetMesaj)


                rvMesajlar.scrollToPosition(myAdapter!!.itemCount)

                var retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var myInterface = retrofit.create(FCMInterface::class.java)
                var headers = HashMap<String,String>()
                headers.put("Content-Type","application/json")
                headers.put("Authorization","key=${SERVER_KEY}")

                FirebaseDatabase.getInstance().reference
                    .child("sohbet_odasi")
                    .child(sohbetOdasiId.toString())
                    .child("sohbet_odasindaki_kullanicilar")
                    .orderByKey().addListenerForSingleValueEvent(object : ValueEventListener{

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            for(users in p0.children){

                                var id = users.key

                                if(!id!!.equals(FirebaseAuth.getInstance().currentUser?.uid.toString())){

                                    FirebaseDatabase.getInstance().reference.child("kullanici")
                                        .orderByKey()
                                        .equalTo(id)
                                        .addListenerForSingleValueEvent(object : ValueEventListener{

                                            override fun onCancelled(p0: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                            override fun onDataChange(p0: DataSnapshot) {

                                                var singleSnapshot = p0.children.iterator().next()

                                                var message_token = singleSnapshot.getValue(Kullanici::class.java)?.mesaj_token

                                                var data = FCMModel.Data()
                                                data.baslik = "Yeni Mesaj Var"
                                                data.bildirimTuru = "sohbet"
                                                data.icerik = etMesaj.text.toString()
                                                data.sohbet_odasi_id = sohbetOdasiId
                                                var to = message_token

                                                var bildirim = FCMModel()
                                                bildirim.to = to
                                                bildirim.data = data

                                                var request = myInterface.bildirimGonder(headers,bildirim)
                                                request.enqueue(object : Callback<Response<FCMModel>>{
                                                    override fun onFailure(call: Call<Response<FCMModel>>, t: Throwable) {
                                                        println("retrofit_error")
                                                    }

                                                    override fun onResponse(call: Call<Response<FCMModel>>, response: Response<Response<FCMModel>>) {
                                                        println("retrofit_succes")
                                                    }

                                                })

                                            }

                                        })


                                }


                            }
                        }

                    })



            }

        }

        etMesaj.setOnClickListener {

            rvMesajlar.smoothScrollToPosition(myAdapter!!.itemCount-1)

        }

    }

    fun getTarih():String{
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr"))
        return sdf.format(Date())
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

            Toast.makeText(this@MesajlasmaActiviyu,"degisti",Toast.LENGTH_SHORT).show()
            sohbetOdasindakiMesajlariGetir()

        }

    }

    fun sohbetOdasindakiMesajlariGetir(){

        if(tumMesajlar == null){
            tumMesajlar = ArrayList()
            myHashSet = HashSet<String>()
        }

        var sorgu = myReference.addValueEventListener(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for(sohbetMesaj in p0.children){

                    var tmpSohbetMesaj = SohbetMesaj()

                    var kullanici_id = sohbetMesaj.getValue(SohbetMesaj::class.java)?.kullanici_id


                    if(!myHashSet!!.contains(sohbetMesaj.key)){

                        myHashSet!!.add(sohbetMesaj.key+"")



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