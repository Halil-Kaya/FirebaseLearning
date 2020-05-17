package com.halilkaya.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthListener()

        btnCikisYap.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            var intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()



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
        super.onResume()
    }


    fun initAuthListener(){

        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kullanici = p0.currentUser

                if(kullanici == null){
                    var intent = Intent(this@MainActivity,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
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
                println("asd")
                return true
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
