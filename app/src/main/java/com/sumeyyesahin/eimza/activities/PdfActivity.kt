package com.sumeyyesahin.eimza.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Canvas
import com.itextpdf.layout.element.Image
import com.sumeyyesahin.eimza.R
import com.sumeyyesahin.eimza.databinding.ActivityPdfBinding
import java.io.File
import java.io.FileOutputStream

class PdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfBinding

    private var pdfUri: Uri? = null
    private var selectedX = 0f
    private var selectedY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.btnSelectPdf.setOnClickListener { selectPdfFile() }

        binding.pageNumberInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                pdfUri?.let {

                    val pageNum = if (binding.pageNumberInput.text.isEmpty()) 1 else (binding.pageNumberInput.text.toString().toIntOrNull() ?: 1)
                    if (pageNum in 1..(getPageCount() ?: 0)) {
                        displayPdfPage(it, pageNum - 1)
                    } else {
                        Toast.makeText(this@PdfActivity, "Geçersiz sayfa numarası.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.pdfImageView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                selectedX = event.x
                selectedY = event.y
                pdfUri?.let { uri ->
                    intent.getStringExtra("signatureUri")?.let { signatureUri ->

                        val pageNumInput = binding.pageNumberInput.text.toString()
                        val pageNum = if (pageNumInput.isEmpty()) 1 else pageNumInput.toInt()
                        addSignatureToPdf(uri, Uri.parse(signatureUri), pageNum, selectedX, selectedY)
                    }
                }
            }
            true
        }

    }

    private fun selectPdfFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pdfSelectLauncher.launch(intent)
    }

    private val pdfSelectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let { uri ->
            pdfUri = uri
            displayPdfPage(uri, 0)
        }
    }

    private fun displayPdfPage(pdfUri: Uri, pageIndex: Int) {
        contentResolver.openFileDescriptor(pdfUri, "r")?.use { fileDescriptor ->
            PdfRenderer(fileDescriptor).use { pdfRenderer ->
                pdfRenderer.openPage(pageIndex).use { page ->
                    val pdfWidth = page.width
                    val pdfHeight = page.height

                    binding.pdfImageView.layoutParams.width = pdfWidth
                    binding.pdfImageView.layoutParams.height = pdfHeight
                    binding.pdfImageView.requestLayout()

                    val bitmap = Bitmap.createBitmap(pdfWidth, pdfHeight, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    binding.pdfImageView.setImageBitmap(bitmap)
                }
            }
        }
    }


    private fun addSignatureToPdf(pdfUri: Uri, signatureUri: Uri, pageNum: Int, touchX: Float, touchY: Float) {
        try {
            contentResolver.openInputStream(pdfUri)?.use { pdfStream ->
                contentResolver.openInputStream(signatureUri)?.use { signatureStream ->
                    val tempFile = File(cacheDir, "signed_document.pdf")
                    val pdfReader = PdfReader(pdfStream)
                    val pdfWriter = PdfWriter(FileOutputStream(tempFile))
                    val pdfDocument = PdfDocument(pdfReader, pdfWriter)

                    if (pageNum in 1..pdfDocument.numberOfPages) {
                        val page = pdfDocument.getPage(pageNum)
                        val pdfWidth = page.pageSize.width
                        val pdfHeight = page.pageSize.height

                        val scaleX = pdfWidth / binding.pdfImageView.width
                        val scaleY = pdfHeight / binding.pdfImageView.height

                        val pdfX = touchX * scaleX
                        val pdfY = pdfHeight - (touchY * scaleY)  // Y koordinatını ters çevirin

                        val imageData = ImageDataFactory.create(signatureStream.readBytes())
                        val canvas = PdfCanvas(page)

                        val image = Image(imageData).apply {
                            setFixedPosition(pdfX-25, pdfY-25)  // İmzanın konumu (X, Y)
                            scaleToFit(100f, 50f)  // İmzanın boyutları
                        }

                        Canvas(canvas, pdfDocument.getDefaultPageSize()).apply {
                            add(image)
                            close()
                        }

                        Toast.makeText(this, "İmza başarıyla eklendi.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Geçersiz sayfa numarası.", Toast.LENGTH_SHORT).show()
                    }

                    pdfDocument.close()
                    viewSignedPdf(tempFile)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "İmza eklenirken hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }



    private fun viewSignedPdf(file: File) {
        val pdfUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(intent, "PDF'yi Görüntüle"))
    }

    private fun getPageCount(): Int? {
        return pdfUri?.let { uri ->
            contentResolver.openFileDescriptor(uri, "r")?.use { fileDescriptor ->
                PdfRenderer(fileDescriptor).use { it.pageCount }
            }
        }
    }
}
