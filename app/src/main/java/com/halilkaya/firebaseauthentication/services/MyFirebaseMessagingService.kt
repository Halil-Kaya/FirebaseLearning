package com.halilkaya.firebaseauthentication.services

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {

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