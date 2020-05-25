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
import com.halilkaya.firebaseauthentication.Fragments.OnayMailiDialogFragment
import com.halilkaya.firebaseauthentication.Fragments.SifreSifirlamaDialogFragment
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    lateinit var myAuthStateListener:FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initMyAuthListener()



        tvKayitOl.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

                var intent = Intent(this@LoginActivity,RegisterActivity::class.java)
                startActivity(intent)

            }

        })


        tvOnaylamaMaili.setOnClickListener(object : View.OnClickListener{

            override fun onClick(v: View?) {

                var onayMailiDialogFragment = OnayMailiDialogFragment()
                onayMailiDialogFragment.show(supportFragmentManager,"onayFragment")


            }

        })

        tvSifremiUnuttum.setOnClickListener {

            var sifreSifirlamaDialogFragment = SifreSifirlamaDialogFragment()
            sifreSifirlamaDialogFragment.show(supportFragmentManager,"sifreSifirlamaFragment")


        }



        btnkullaniciyiGetir.setOnClickListener {

            Toast.makeText(this,"suanki Kullanici: ${FirebaseAuth.getInstance().currentUser?.email}",Toast.LENGTH_LONG).show()

        }

    }

    override fun onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListener)
        super.onStart()
    }

    override fun onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(myAuthStateListener)
        super.onDestroy()
    }


    fun initMyAuthListener(){


        myAuthStateListener = object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                println("init")
                var kullanici = p0.currentUser

                if(kullanici != null){
                    println("mail: "+kullanici.isEmailVerified)
                    if(kullanici.isEmailVerified){

                        Toast.makeText(this@LoginActivity,"mail onaylandi giris yapılıyor   ",Toast.LENGTH_SHORT).show()
                        var intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else{
                        Toast.makeText(this@LoginActivity,"mailinizi onaylayin",Toast.LENGTH_SHORT).show()

                    }

                }else{
                    println("giriş yada çıkış yapıldı<Kullanıcı yok>")
                }


            }

        }



    }


    fun btnGirisYap(view:View){

        if(etTelNo.text.isNullOrEmpty() || etSifre.text.isNullOrEmpty()){

            Toast.makeText(this,"Bilgileri giriniz",Toast.LENGTH_SHORT).show()

        }else{


            girisYap(etTelNo.text.toString(),etSifre.text.toString())



        }


    }

    fun girisYap(mail:String,sifre:String){

        progressBarGoster()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail,sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {

                    progressBarGizle()
                    if(p0.isSuccessful && FirebaseAuth.getInstance().currentUser?.isEmailVerified == true){
                        var intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()
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
