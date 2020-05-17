package com.halilkaya.firebaseauthentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.*

class HesapAyarlariActivity : AppCompatActivity() {

    var kullanici = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap_ayarlari)



        etName.setText(kullanici?.displayName)
        etMail.setText(kullanici?.email)


        btnSifreSifirla.setOnClickListener {

            FirebaseAuth.getInstance().sendPasswordResetEmail(kullanici?.email.toString())
                .addOnCompleteListener(object : OnCompleteListener<Void>{
                    override fun onComplete(p0: Task<Void>) {
                        if(p0.isSuccessful){
                            Toast.makeText(this@HesapAyarlariActivity,"mailinize şifre gönderildi",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@HesapAyarlariActivity,"bir hata oldu mailinizi kontrol edin",Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }


        btnDegisikleriKaydet.setOnClickListener {


            if(etName.text.isNotEmpty() && etMail.text.isNotEmpty()){

                if(!etName.text.toString().equals(kullanici?.displayName.toString())){

                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                        .setDisplayName(etName.text.toString())
                        .build()
                    kullanici?.updateProfile(bilgileriGuncelle)
                    Toast.makeText(this,"bilgiler guncellendi",Toast.LENGTH_SHORT).show()
                }


            }else{
                Toast.makeText(this,"bilgileri doldurunuz",Toast.LENGTH_SHORT).show()
            }



        }



    }
}





