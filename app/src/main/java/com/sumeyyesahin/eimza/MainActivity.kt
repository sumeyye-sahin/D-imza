package com.sumeyyesahin.eimza

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.io.image.ImageDataFactory
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val btnAddSignature = findViewById<Button>(R.id.btnAddSignature)
        btnAddSignature.setOnClickListener {

            val intent = Intent(this, SignatureActivity::class.java)
            startActivity(intent)
            deleteSignatureFile()
        }
    }

    private fun deleteSignatureFile() {
        val signatureFile = File(filesDir, "signature.png") // Dosya yolunu belirtin

        if (signatureFile.exists()) {
            if (signatureFile.delete()) {
                //Toast.makeText(this, "İmza dosyası başarıyla silindi.", Toast.LENGTH_SHORT).show()
            } else {
               // Toast.makeText(this, "İmza dosyası silinirken bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }
        } else {
            //Toast.makeText(this, "Silinecek imza dosyası bulunamadı.", Toast.LENGTH_SHORT).show()
        }
    }

}

