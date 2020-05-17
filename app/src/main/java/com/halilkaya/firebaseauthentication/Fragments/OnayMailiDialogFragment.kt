package com.halilkaya.firebaseauthentication.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.halilkaya.firebaseauthentication.R

class OnayMailiDialogFragment(var mContex:Context) : DialogFragment() {

    lateinit var etMail:EditText
    lateinit var etSifre:EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.onaymaili_diaolgfragment,container,false)

        this.isCancelable = false

        etMail = view.findViewById(R.id.etMail)
        etSifre = view.findViewById(R.id.etSifre)


        var btnGonder = view.findViewById<Button>(R.id.btnGonder)
        btnGonder.setOnClickListener {

            if(etMail.text.isNullOrEmpty() || etSifre.text.isNullOrEmpty()){

                Toast.makeText(mContex,"bilgileri giriniz",Toast.LENGTH_SHORT).show()

            }else{

                girisYapVeMailGonder(etMail.text.toString(),etSifre.text.toString())

            }


        }


        var btnIptal = view.findViewById<Button>(R.id.btnIptal)
        btnIptal.setOnClickListener {
            dismiss()
        }

        return view
    }


    fun girisYapVeMailGonder(mail:String,sifre:String){

        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail,sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{

                override fun onComplete(p0: Task<AuthResult>) {

                    if(p0.isSuccessful){
                        mailGonder()
                    }else{
                        Toast.makeText(mContex,"bir hata oldu",Toast.LENGTH_SHORT).show()
                    }


                }

            })

    }

    fun mailGonder(){

        var kullanici = FirebaseAuth.getInstance().currentUser

        if(kullanici != null){


            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void>{


                    override fun onComplete(p0: Task<Void>) {

                        if(p0.isSuccessful){
                            Toast.makeText(mContex,"onaylama maili gonderildi",Toast.LENGTH_SHORT).show()
                            dismiss()
                        }else{
                            Toast.makeText(mContex,"onaylama maili gonderilmedi",Toast.LENGTH_SHORT).show()
                        }

                        FirebaseAuth.getInstance().signOut()

                    }


                })



        }


    }

}