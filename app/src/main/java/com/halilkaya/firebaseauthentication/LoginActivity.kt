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
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.time.Instant

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)




        tvKayitOl.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

                var intent = Intent(this@LoginActivity,RegisterActivity::class.java)
                startActivity(intent)

            }

        })



        btnkullaniciyiGetir.setOnClickListener {

            Toast.makeText(this,"suanki Kullanici: ${FirebaseAuth.getInstance().currentUser?.email}",Toast.LENGTH_LONG).show()

        }

    }


    fun btnGirisYap(view:View){

        if(etMail.text.isNullOrEmpty() || etSifre.text.isNullOrEmpty()){

            Toast.makeText(this,"Bilgileri giriniz",Toast.LENGTH_SHORT).show()

        }else{


            girisYap(etMail.text.toString(),etSifre.text.toString())



        }


    }

    fun girisYap(mail:String,sifre:String){

        progressBarGoster()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail,sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {

                    progressBarGizle()
                    if(p0.isSuccessful){

                        Toast.makeText(this@LoginActivity,"giris basarili: ${FirebaseAuth.getInstance().currentUser?.email}",Toast.LENGTH_SHORT).show()

                    }else{

                        Toast.makeText(this@LoginActivity,"islem basarisiz: ${p0.exception?.message}",Toast.LENGTH_LONG).show()

                    }
                }
            })


    }



    fun progressBarGoster(){
        progressBarLogin.visibility = View.VISIBLE
    }

    fun progressBarGizle(){
        progressBarLogin.visibility = View.INVISIBLE
    }


}
