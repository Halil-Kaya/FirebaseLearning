package com.halilkaya.firebaseauthentication.Model

class SohbetOdasi {

    var sohbet_odasi_adi:String = ""
    var olusturan_id:String = ""
    var seviye:String = ""
    var sohbet_odasi_id = ""

    constructor(){

    }

    constructor(sohbet_odasi_adi:String,olusturan_id:String,seviye:String,sohbet_odasi_id:String){
        this.sohbet_odasi_adi = sohbet_odasi_adi
        this.olusturan_id = olusturan_id
        this.seviye = seviye
        this.sohbet_odasi_id = sohbet_odasi_id
    }

}