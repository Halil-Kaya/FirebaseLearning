package com.halilkaya.firebaseauthentication.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.halilkaya.firebaseauthentication.R

class ProfilResmiFragment : DialogFragment(){

    lateinit var tvKameradan:TextView
    lateinit var tvGaleriden:TextView

    lateinit var mProfilResmiListener: onProfilResmiListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.profil_resmi_nerden,container,false)



        tvGaleriden = view.findViewById(R.id.tvGaleriden)
        tvGaleriden.setOnClickListener {

            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent,100)



        }


        tvKameradan = view.findViewById(R.id.tvKameradan)
        tvKameradan.setOnClickListener {

            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,200)

        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        //galeriden resim seciliyor
        if(requestCode == 100 && resultCode == Activity.RESULT_OK && data != null){


            var galeridenSecilenResimYolu = data.data
            mProfilResmiListener.getResimYolu(galeridenSecilenResimYolu)
            dismiss()

        }//kameradan resim seciliyor
        else if(requestCode == 200 && resultCode == Activity.RESULT_OK && data != null){

            var kameradanCekilenResim:Bitmap
            kameradanCekilenResim = data.extras?.get("data") as Bitmap

            mProfilResmiListener.getResimBitmap(kameradanCekilenResim)
            dismiss()

        }


    }

    override fun onAttach(context: Context) {
        mProfilResmiListener = activity as onProfilResmiListener
        super.onAttach(context)
    }


}

interface onProfilResmiListener {

    fun getResimYolu(resimPath:Uri?)
    fun getResimBitmap(bitmap:Bitmap)

}