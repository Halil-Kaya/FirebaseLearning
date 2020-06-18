package com.halilkaya.firebaseauthentication.services

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.halilkaya.firebaseauthentication.MesajlasmaActiviyu
import com.halilkaya.firebaseauthentication.Model.SohbetOdasi
import com.halilkaya.firebaseauthentication.R
import java.util.*
import kotlin.collections.HashMap

class MyFirebaseMessagingService : FirebaseMessagingService() {

    var okunmayiBekleyenMesajSayisi = 0

    override fun onMessageReceived(p0: RemoteMessage) {

        if(!activityiKontrolEt()){


        var bildirimBaslik = p0.notification?.title
        var bildirimBody = p0.notification?.body
        var data = p0.data
        var baslik = data.get("baslik")
        var icerik = data.get("icerik")
        var bildirimTuru = data.get("bildirimTuru")
        var sohbet_odasi_id = data.get("sohbet_odasi_id")

        println("--------------------")
        println("baslik: ${baslik}")
        println("icerik: ${icerik}")
        println("bildirimTuru: ${bildirimTuru}")
        println("sohbet_odasi_id: ${sohbet_odasi_id}")
        println("--------------------")

        var ref = FirebaseDatabase.getInstance().reference
            .child("sohbet_odasi")
            .orderByKey()
            .equalTo(sohbet_odasi_id).addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var tekSohbetOdasi = p0.children.iterator().next()

                    var oAnKiSohbetOdasi = SohbetOdasi()

                    var nesneMap = (tekSohbetOdasi.getValue() as HashMap<String,Object>)

                    oAnKiSohbetOdasi.sohbet_odasi_id = nesneMap.get("sohbet_odasi_id").toString()
                    oAnKiSohbetOdasi.olusturan_id = nesneMap.get("olusturan_id").toString()
                    oAnKiSohbetOdasi.seviye = nesneMap.get("seviye").toString()
                    oAnKiSohbetOdasi.sohbet_odasi_adi = nesneMap.get("sohbet_odasi_adi").toString()

                    var gorulenMesajSayisi = tekSohbetOdasi
                        .child("sohbet_odasindaki_kullanicilar")
                        .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .child("okunmamis_mesaj_sayisi")
                        .getValue().toString().toInt()

                    var toplamMesajSayisi = tekSohbetOdasi.child("sohbet_odasi_mesajlari")
                        .childrenCount.toInt()

                    okunmayiBekleyenMesajSayisi = toplamMesajSayisi - gorulenMesajSayisi

                    bildirimGonder(baslik,icerik,oAnKiSohbetOdasi)

                }

            })

        }


    }

    fun bildirimGonder(baslik:String?,icerik:String?,oAnKiSohbetOdasi:SohbetOdasi){

        var bildirimID = notificationIDolustur(oAnKiSohbetOdasi.sohbet_odasi_id)

        var builder = NotificationCompat.Builder(this,oAnKiSohbetOdasi.sohbet_odasi_adi)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(oAnKiSohbetOdasi.sohbet_odasi_adi+"odasindan " +baslik)
            .setAutoCancel(true)
            .setSubText("okunmayi bekleyen mesaj sayisi: "+okunmayiBekleyenMesajSayisi)
            .setOnlyAlertOnce(true)

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(bildirimID,builder.build())

    }

    fun activityiKontrolEt():Boolean{

        if(MesajlasmaActiviyu.activityAcikMi){
            return true
        }
        return false

    }

    fun notificationIDolustur(sohbetOdasiID:String):Int{

        var id = 0

        for(i in 4..8){

            id+=sohbetOdasiID[i].toInt()

        }


        return id
    }


    override fun onNewToken(p0: String) {

        val refreshedToken = p0

        var ref = FirebaseDatabase.getInstance().reference
        ref.child("kullanici")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("mesaj_token")
            .child(refreshedToken)

    }


}