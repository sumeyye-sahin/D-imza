package com.sumeyyesahin.eimza.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.sumeyyesahin.eimza.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
           binding = ActivityMainBinding.inflate(layoutInflater)
              setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding.btnAddSignature.setOnClickListener {

            val intent = Intent(this, SignatureActivity::class.java)
            startActivity(intent)
            deleteSignatureFile()
        }
    }

    private fun deleteSignatureFile() {
        val signatureFile = File(filesDir, "signature.png") // Dosya yolunu belirtin

        if (signatureFile.exists()) {
            signatureFile.delete()
        }
    }

}

