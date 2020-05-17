package com.halilkaya.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnCikisYap.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            var intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()



        }


    }




}
