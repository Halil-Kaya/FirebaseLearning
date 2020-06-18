package com.halilkaya.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.halilkaya.firebaseauthentication.Model.Kullanici
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


    }




    fun kayitOl(view:View){

        if(etTelNo.text.isNullOrEmpty() || etSifre.text.isNullOrEmpty() || etSifreTekrar.text.isNullOrEmpty()){
            Toast.makeText(this,"bilgileri giriniz",Toast.LENGTH_SHORT).show()
        }else{

            if(etSifre.text.toString().equals(etSifreTekrar.text.toString())){

                yeniUyeKayit(etTelNo.text.toString(),etSifre.text.toString())

            }else{

                Toast.makeText(this,"sifreler uyusmuyor",Toast.LENGTH_SHORT).show()

            }

        }

    }


    fun yeniUyeKayit(mail:String,sifre:String){

        progressBarGoster()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {


                    if(p0.isSuccessful){

                      var veriTabaninaEklenecekKullanici = Kullanici()

                        veriTabaninaEklenecekKullanici.isim = etTelNo.text.toString().substring(0,etTelNo.text.indexOf('@'))
                        veriTabaninaEklenecekKullanici.kullanici_id = p0.result?.user?.uid
                        veriTabaninaEklenecekKullanici.profil_resmi = ""
                        veriTabaninaEklenecekKullanici.telefon = "123"
                        veriTabaninaEklenecekKullanici.seviye = "1"

                        FirebaseDatabase.getInstance().reference
                            .child("kullanici")
                            .child(FirebaseAuth.getInstance().currentUser?.uid+"")
                            .setValue(veriTabaninaEklenecekKullanici)
                            .addOnCompleteListener(object : OnCompleteListener<Void>{

                                override fun onComplete(p0: Task<Void>) {
                                    if(p0.isSuccessful){


                                        FirebaseAuth.getInstance().signOut()
                                        var intent = Intent(this@RegisterActivity,LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()


                                    }else{
                                        Toast.makeText(this@RegisterActivity,"hata: ${p0.exception?.message}",Toast.LENGTH_LONG).show()
                                    }
                                }

                            })



                        onaylamaMailiGonder()
                        progressBarGizle()

                    }else{
                        progressBarGizle()
                        Toast.makeText(this@RegisterActivity,"Kayit olunamadi seveb: ${p0.exception?.message}",Toast.LENGTH_LONG).show()

                    }

                }
            })



    }


    fun onaylamaMailiGonder(){


        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici != null){

            println("kapı3")
            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void>{
                    override fun onComplete(p0: Task<Void>) {
                        println("kapı4")

                        progressBarGizle()
                        if(p0.isSuccessful){
                            println("kapı5")
                            Toast.makeText(this@RegisterActivity,"onaylama maili gonderildi",Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(this@RegisterActivity,"mail gonderme islemi basarisiz: ${p0.exception?.message}",Toast.LENGTH_LONG).show()

                        }

                        FirebaseAuth.getInstance().signOut()

                    }

                })


        }




    }


    fun progressBarGoster(){

        progressBar.visibility = View.VISIBLE

    }

    fun progressBarGizle(){
        progressBar.visibility = View.INVISIBLE
    }


}












