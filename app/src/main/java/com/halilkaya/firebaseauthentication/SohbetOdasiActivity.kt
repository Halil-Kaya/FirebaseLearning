package com.halilkaya.firebaseauthentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.halilkaya.firebaseauthentication.Fragments.YeniSohbetOdasiDialogFragment
import kotlinx.android.synthetic.main.activity_sohbet_odasi.*

class SohbetOdasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_odasi)

        fabtnYeniSohbetOdasi.setOnClickListener {

            var myYeniSohbetOdasi = YeniSohbetOdasiDialogFragment()
            myYeniSohbetOdasi.show(supportFragmentManager,"frag-YeniOda")

        }

    }
}
