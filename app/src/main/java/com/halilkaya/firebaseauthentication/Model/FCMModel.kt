package com.halilkaya.firebaseauthentication.Model

class FCMModel {
    var data: Data? = null
    var to: String? = null

    class Data {
        var bildirimTuru: String? = null
        var icerik: String? = null
        var baslik: String? = null
        var sohbet_odasi_id: String? = null

    }
}