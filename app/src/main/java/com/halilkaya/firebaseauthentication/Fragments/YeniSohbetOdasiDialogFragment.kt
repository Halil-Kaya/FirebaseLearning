package com.halilkaya.firebaseauthentication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.firebaseauthentication.Model.Kullanici
import com.halilkaya.firebaseauthentication.Model.SohbetMesaj
import com.halilkaya.firebaseauthentication.Model.SohbetOdasi
import com.halilkaya.firebaseauthentication.R
import com.halilkaya.firebaseauthentication.SohbetOdasiActivity
import kotlinx.android.synthetic.main.activity_sohbet_odasi.*
import java.text.SimpleDateFormat
import java.util.*

class YeniSohbetOdasiDialogFragment : DialogFragment() {

    lateinit var etSohbetOdasiAdi:EditText
    lateinit var sbSeviye:SeekBar
    lateinit var tvSeviye:TextView
    lateinit var btnOlustur:Button
    var mySeekBarProgress = 0
    var kullaniciSeviyesi = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.yeni_sohbet_odasi_dialogfragment,container,false)
        kullaniciSeviyesiBilgi()

        etSohbetOdasiAdi = view.findViewById(R.id.etSohbetOdasiAdi)
        sbSeviye = view.findViewById(R.id.sbSeviye)
        tvSeviye = view.findViewById(R.id.tvSeviye)
        btnOlustur = view.findViewById(R.id.btnOlustur)


        sbSeviye.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mySeekBarProgress = progress
                tvSeviye.setText("$mySeekBarProgress")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        btnOlustur.setOnClickListener {


            var rootLayout = activity?.rootLayout as CoordinatorLayout

            if(etSohbetOdasiAdi.text.isNullOrEmpty()){

                var snackbar = Snackbar.make(rootLayout,"Sohbet Adini Giriniz",Snackbar.LENGTH_SHORT)
                snackbar.show()
            }else{

                if(kullaniciSeviyesi >= mySeekBarProgress){

                    var ref = FirebaseDatabase.getInstance().reference

                    var sohbetOdasiID = ref.child("sohbet_odasi").push().key!!

                    var yeniSohbetOdasi = SohbetOdasi()

                    yeniSohbetOdasi.olusturan_id = FirebaseAuth.getInstance().currentUser?.uid!!
                    yeniSohbetOdasi.seviye = mySeekBarProgress.toString()
                    yeniSohbetOdasi.sohbet_odasi_adi = etSohbetOdasiAdi.text.toString()
                    yeniSohbetOdasi.sohbet_odasi_id = sohbetOdasiID.toString()

                    ref.child("sohbet_odasi").child(sohbetOdasiID).setValue(yeniSohbetOdasi)

                    var mesajID = ref.child("sohbet_odasi").push().key

                    var karsilamaMesaji = SohbetMesaj()
                    karsilamaMesaji.mesaj = "bu ilk mesaj hos geldin bravo six going dark"
                    karsilamaMesaji.time = getTarih()

                    ref.child("sohbet_odasi")
                        .child(sohbetOdasiID)
                        .child("sohbet_odasi_mesajlari")
                        .child(mesajID.toString())
                        .setValue(karsilamaMesaji)

                    var snackbar = Snackbar.make(rootLayout,"Sohbet Odasi Olusturuldu",Snackbar.LENGTH_SHORT)
                    snackbar.show()

                    (activity as SohbetOdasiActivity).init()
                    dismiss()


                }else{

                    var snackbar = Snackbar.make(rootLayout,"Seviyeniz yetmiyor, Sizin Seviyeniz: ${kullaniciSeviyesi}",Snackbar.LENGTH_LONG)
                    snackbar.show()


                }

            }



        }


        return view
    }


    fun getTarih():String{
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr"))
        return sdf.format(Date())
    }


    fun kullaniciSeviyesiBilgi(){

        var ref = FirebaseDatabase.getInstance().reference

        var query = ref.child("kullanici").orderByKey().equalTo(FirebaseAuth.getInstance().currentUser?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(user in p0.children){

                    kullaniciSeviyesi = user.getValue(Kullanici::class.java)?.seviye!!.toInt()

                }


            }

        })


    }

}