package com.halilkaya.firebaseauthentication

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.halilkaya.firebaseauthentication.Fragments.ProfilResmiFragment
import com.halilkaya.firebaseauthentication.Fragments.onProfilResmiListener
import com.halilkaya.firebaseauthentication.Model.Kullanici
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.*
import java.io.ByteArrayOutputStream

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

    inner class BackgroundResimCompress : AsyncTask<Uri,Void,ByteArray>{

        var myBitmap:Bitmap? = null


        constructor(){}
        constructor(bm:Bitmap){

            if(bm != null) {
                myBitmap = bm
            }

        }


        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Uri?): ByteArray {

            if(myBitmap == null){
                myBitmap = MediaStore.Images.Media.getBitmap(this@HesapAyarlariActivity.contentResolver,params[0])
            }

            var resimBytes:ByteArray? = null

            for(i in 1..5){
                resimBytes = convertBitmaptoByte(myBitmap,100/i)
            }

            return resimBytes!!

        }

        fun convertBitmaptoByte(myBitmap:Bitmap?,i:Int):ByteArray?{

            var stream = ByteArrayOutputStream()
            myBitmap?.compress(Bitmap.CompressFormat.JPEG,i,stream)
            return stream.toByteArray()

        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)

            uploadResimToFirebase(result)

        }


    }

    fun uploadResimToFirebase(result : ByteArray?){

        var storageReferans = FirebaseStorage.getInstance().reference
        var resimEklenecekYer = storageReferans.child("images/users"+kullanici?.uid+"/profile_resim")


        var uploadGorevi = resimEklenecekYer.putBytes(result!!)

        uploadGorevi.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot>{

            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {

                resimEklenecekYer.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri>{
                    override fun onSuccess(p0: Uri?) {


                        var firebaseUrl = p0.toString()


                        Toast.makeText(this@HesapAyarlariActivity,""+firebaseUrl,Toast.LENGTH_LONG).show()

                        FirebaseDatabase.getInstance().reference
                            .child("kullanici")
                            .child(kullanici?.uid!!)
                            .child("profil_resmi")
                            .setValue(firebaseUrl.toString())




                    }

                })


            }


        })

    }

    fun fotografCompressed(galeridenGelenUri:Uri){
        var compressed = BackgroundResimCompress()
        compressed.execute(galeridenGelenUri)


    }

    fun fotografCompressed(kameradanGelenBitmap: Bitmap){

        var compresed = BackgroundResimCompress()
        var uri:Uri? = null
        compresed.execute(uri)


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

            if(galeridenGelenUri != null){

                fotografCompressed(galeridenGelenUri!!)

            }else if(kameradanGelenBitmap != null){

                fotografCompressed(kameradanGelenBitmap!!)

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
                    Picasso.get().load(okunanKullanici?.profil_resmi).resize(100,100).into(imgProfilResmi)

                }

            }


        })










    }




}





