package com.halilkaya.firebaseauthentication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.halilkaya.firebaseauthentication.R

class SifreSifirlamaDialogFragment : DialogFragment() {

    lateinit var etMail:EditText
    var mContex:FragmentActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.sifresifirlama_dialogfragment,container,false)
        this.isCancelable = false

        mContex = activity

        etMail = view.findViewById(R.id.etMail)

        var btnGonder = view.findViewById<Button>(R.id.btnGonder)
        btnGonder.setOnClickListener {

            if(etMail.text.isNullOrEmpty()){

                Toast.makeText(mContex,"mail giriniz",Toast.LENGTH_SHORT).show()

            }else{

                sifreSifirla(etMail.text.toString())

            }

        }

        var btnIptal = view.findViewById<Button>(R.id.btnIptal)
        btnIptal.setOnClickListener {
            dismiss()
        }


        return view
    }


    fun sifreSifirla(mail:String){


        FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
            .addOnCompleteListener(object : OnCompleteListener<Void>{

                override fun onComplete(p0: Task<Void>) {

                    if(p0.isSuccessful){

                        Toast.makeText(mContex,"sifre sifirlama maili gonderildi",Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(mContex,"hata boyle bir mail yok",Toast.LENGTH_SHORT).show()
                    }
                    dismiss()

                }

            })



    }


}