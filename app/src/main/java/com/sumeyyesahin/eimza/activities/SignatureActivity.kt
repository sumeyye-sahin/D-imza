package com.sumeyyesahin.eimza.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import com.sumeyyesahin.eimza.R
import com.sumeyyesahin.eimza.databinding.ActivitySignatureBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SignatureActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        binding.btnSaveSignature.setOnClickListener {

            if(binding.signatureView.path.isEmpty()){
                Toast.makeText(this, "İmza atmadınız.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{

                val signatureBitmap = binding.signatureView.getSignatureBitmap()
                saveSignatureAsPng(signatureBitmap)
                binding.signatureView.clearSignature()
            }


        }

        binding.btnShowSignature.setOnClickListener { loadSignature() }
        binding.btnDeleteSignature.setOnClickListener { deleteSignature() }

        binding.btnSelectPdf.setOnClickListener {
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
                binding.signatureImageView.setImageBitmap(bitmap)
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

                binding.signatureImageView.setImageBitmap(null)

                Toast.makeText(this, "İmza başarıyla silindi.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "İmza silinirken bir hata oluştu.", Toast.LENGTH_LONG).show()
            }
        } else {
            binding.signatureView.clearSignature()
        }
    }
}
