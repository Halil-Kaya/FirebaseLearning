package com.halilkaya.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthListener()
        initFCM()
        getPendingIntent()
    }

    fun getPendingIntent(){

        var gelenIntent = intent

        if(gelenIntent.hasExtra("sohbet_odasi_id")){
            var intent = Intent(this,MesajlasmaActiviyu::class.java)
            intent.putExtra("sohbet_odasi_id",gelenIntent.getStringExtra("sohbet_odasi_id"))
            startActivity(intent)
        }


    }

    fun initFCM(){
        var ref = FirebaseDatabase.getInstance().reference

         FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener{idResult ->
            Toast.makeText(this,"token kismi aktif",Toast.LENGTH_SHORT).show()
            var token = idResult.token

             Toast.makeText(this,"token: ${token}",Toast.LENGTH_SHORT).show()

             ref.child("kullanici")
                 .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                 .child("mesaj_token")
                 .setValue(token)

        }


    }


    override fun onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
        super.onStart()
    }

    override fun onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
        super.onDestroy()
    }

    override fun onResume() {
        kullaniciKontorlEt()
        setBilgiler()
        super.onResume()
    }


    fun setBilgiler(){
        tvKullaniciAdi.setText(FirebaseAuth.getInstance().currentUser?.displayName)
        tvMail.setText(FirebaseAuth.getInstance().currentUser?.email)
        tvUserID.setText(FirebaseAuth.getInstance().currentUser?.uid)

    }



    fun initAuthListener(){

        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kullanici = p0.currentUser

                if(kullanici == null){
                    var intent = Intent(this@MainActivity,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{

                }


            }

        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.cikisYap -> {
                cikisYap()
                return true
            }
            R.id.hesapAyarlari -> {

                var intent = Intent(this,HesapAyarlariActivity::class.java)
                startActivity(intent)


            }

            R.id.sohbetOdasi -> {
                var intent = Intent(this,SohbetOdasiActivity::class.java)
                startActivity(intent)
            }


        }



        return super.onOptionsItemSelected(item)
    }

    fun kullaniciKontorlEt(){

        var kullanici = FirebaseAuth.getInstance().currentUser
        if(kullanici == null){
            cikisYap()
        }

    }


    fun cikisYap(){
        FirebaseAuth.getInstance().signOut()

    }


}
