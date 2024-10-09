package com.sumeyyesahin.eimza

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SignatureActivity : AppCompatActivity() {
    private lateinit var signatureView: SignatureView
    private lateinit var signatureImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        signatureView = findViewById(R.id.signatureView)
        signatureImageView = findViewById(R.id.signatureImageView)

        val btnSaveSignature = findViewById<Button>(R.id.btnSaveSignature)
        val btnShowSignature = findViewById<Button>(R.id.btnShowSignature)
        val btnDeleteSignature = findViewById<Button>(R.id.btnDeleteSignature)
        val btnSelectPdf = findViewById<Button>(R.id.btnSelectPdf)

        btnSaveSignature.setOnClickListener {

            if(signatureView.path.isEmpty()){
                Toast.makeText(this, "İmza atmadınız.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{

                val signatureBitmap = signatureView.getSignatureBitmap()
                saveSignatureAsPng(signatureBitmap)
                signatureView.clearSignature()
            }


        }

        btnShowSignature.setOnClickListener { loadSignature() }
        btnDeleteSignature.setOnClickListener { deleteSignature() }

        btnSelectPdf.setOnClickListener {
            val signatureFile = File(filesDir, "signature.png")
            if (signatureFile.exists()) {
                val signatureUri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.provider",
                    signatureFile
                )

                val intent = Intent(this, PdfActivity::class.java).apply {
                    putExtra("signatureUri", signatureUri.toString())
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Önce imza kaydedin.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // İmza kaydetme fonksiyonu
    private fun saveSignatureAsPng(bitmap: Bitmap) {
        try {
            val file = File(filesDir, "signature.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this, "İmza başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "İmza kaydedilirken bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // İmza yükleme fonksiyonu
    private fun loadSignature() {
        val file = File(filesDir, "signature.png")
        if (file.exists()) {
            try {
                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                signatureImageView.setImageBitmap(bitmap)
                Toast.makeText(this, "İmza başarıyla yüklendi.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "İmza yüklenirken bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Kaydedilmiş bir imza bulunamadı.", Toast.LENGTH_SHORT).show()
        }
    }

    // İmza silme fonksiyonu
    private fun deleteSignature() {

        val file = File(filesDir, "signature.png")
        if (file.exists()) {
            if (file.delete()) {

                signatureImageView.setImageBitmap(null)

                Toast.makeText(this, "İmza başarıyla silindi.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "İmza silinirken bir hata oluştu.", Toast.LENGTH_LONG).show()
            }
        } else {
            signatureView.clearSignature()
        }
    }
}
