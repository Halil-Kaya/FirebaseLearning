package com.halilkaya.firebaseauthentication

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.halilkaya.firebaseauthentication.Fragments.ProfilResmiFragment
import com.halilkaya.firebaseauthentication.Fragments.onProfilResmiListener
import com.halilkaya.firebaseauthentication.Model.Kullanici
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.*

class HesapAyarlariActivity : AppCompatActivity(),onProfilResmiListener {

    var kullanici = FirebaseAuth.getInstance().currentUser

    var izinlerVerildiMi:Boolean = false


    var galeridenGelenUri:Uri? = null
    var kameradanGelenBitmap:Bitmap? = null

    override fun getResimYolu(resimPath: Uri?) {

        galeridenGelenUri = resimPath
        Picasso.get().load(galeridenGelenUri).resize(100,100).into(imgProfilResmi)

    }

    override fun getResimBitmap(bitmap: Bitmap) {

        kameradanGelenBitmap = bitmap
        imgProfilResmi.setImageBitmap(kameradanGelenBitmap)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap_ayarlari)
        setKullaniciBilgileri()


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

            if(etName.text.isNotEmpty() && etTelNo.text.isNotEmpty()){

                if(!etName.text.toString().equals(kullanici?.displayName.toString())){

                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                        .setDisplayName(etName.text.toString())
                        .build()

                    kullanici!!.updateProfile(bilgileriGuncelle).addOnCompleteListener(object : OnCompleteListener<Void>{

                        override fun onComplete(p0: Task<Void>) {
                            if(p0.isSuccessful){
                                FirebaseDatabase.getInstance().reference
                                    .child("kullanici")
                                    .child(kullanici!!.uid)
                                    .child("isim")
                                    .setValue(etName.text.toString())
                                    .addOnCompleteListener(object : OnCompleteListener<Void>{
                                        override fun onComplete(p0: Task<Void>) {
                                            if(p0.isSuccessful){

                                                Toast.makeText(this@HesapAyarlariActivity,"isim guncellendi",Toast.LENGTH_SHORT).show()

                                            }else{

                                                Toast.makeText(this@HesapAyarlariActivity,"isim-hata: ${p0.exception?.message}",Toast.LENGTH_SHORT).show()

                                            }
                                        }
                                    })
                            }
                        }
                    })
                }

            }else{
                Toast.makeText(this,"bilgileri doldurunuz",Toast.LENGTH_SHORT).show()
            }

            if(etTelNo.text.isNotEmpty()){

                FirebaseDatabase.getInstance().reference
                    .child("kullanici")
                    .child(kullanici!!.uid)
                    .child("telefon")
                    .setValue(etTelNo.text.toString()).addOnCompleteListener(object : OnCompleteListener<Void>{


                        override fun onComplete(p0: Task<Void>) {

                            if(p0.isSuccessful){

                                Toast.makeText(this@HesapAyarlariActivity,"tel guncellendi",Toast.LENGTH_SHORT).show()

                            }else{

                                Toast.makeText(this@HesapAyarlariActivity,"tell-eror: ${p0.exception?.message}",Toast.LENGTH_SHORT).show()

                            }
                        }
                    })

            }

        }



        imgProfilResmi.setOnClickListener {

            if(izinlerVerildiMi) {
                var profilResmiFragment = ProfilResmiFragment()
                profilResmiFragment.show(supportFragmentManager, "frag-ok")
            }else{
                izinleriIste()
            }



        }






    }

    fun izinleriIste(){

        var izinler = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA)

        if(ContextCompat.checkSelfPermission(this,izinler[0]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this,izinler[1]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this,izinler[2]) == PackageManager.PERMISSION_GRANTED){

            izinlerVerildiMi = true
        }else{
            ActivityCompat.requestPermissions(this,izinler,150)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if(requestCode == 150){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){

                var profilResmiFragment = ProfilResmiFragment()
                profilResmiFragment.show(supportFragmentManager, "fotoSec")
            }else{

                Toast.makeText(this,"tum izinleri vermelisiniz",Toast.LENGTH_SHORT).show()

            }


        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    fun setKullaniciBilgileri(){


        var kullanici = FirebaseAuth.getInstance().currentUser
        var reference = FirebaseDatabase.getInstance().reference


        var sorgu = reference.child("kullanici")
            .orderByKey()
            .equalTo(kullanici?.uid)

        sorgu.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@HesapAyarlariActivity,"hata: ${p0.message}",Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(snapshot in p0.children){

                    var okunanKullanici = snapshot.getValue(Kullanici::class.java)
                    etName.setText(okunanKullanici?.isim)
                    etTelNo.setText(okunanKullanici?.telefon)


                }

            }


        })










    }




}





