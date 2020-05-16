package com.halilkaya.firebaseauthentication

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



    }



    fun kayitOl(view:View){

        if(etMail.text.isNullOrEmpty() || etSifre.text.isNullOrEmpty() || etSifreTekrar.text.isNullOrEmpty()){
            Toast.makeText(this,"bilgileri giriniz",Toast.LENGTH_SHORT).show()
        }else{

            if(etSifre.text.toString().equals(etSifreTekrar.text.toString())){

                yeniUyeKayit(etMail.text.toString(),etSifre.text.toString())

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

                        Toast.makeText(this@RegisterActivity,"kaydedildi maili: ${FirebaseAuth.getInstance().currentUser?.email}",Toast.LENGTH_LONG).show()

                        onaylamaMailiGonder()
                        FirebaseAuth.getInstance().signOut()


                    }else{

                        Toast.makeText(this@RegisterActivity,"Kayit olunamadi seveb: ${p0.exception?.message}",Toast.LENGTH_LONG).show()

                    }

                }
            })



    }


    fun onaylamaMailiGonder(){


        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici != null){


            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void>{

                    override fun onComplete(p0: Task<Void>) {

                        progressBarGizle()
                        if(p0.isSuccessful){

                            Toast.makeText(this@RegisterActivity,"onaylama maili gonderildi",Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(this@RegisterActivity,"mail gonderme islemi basarisiz: ${p0.exception?.message}",Toast.LENGTH_LONG).show()
                        }

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












