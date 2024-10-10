package com.sumeyyesahin.eimza.signatureView
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SignatureView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 8f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(event.x, event.y)
            MotionEvent.ACTION_MOVE -> path.lineTo(event.x, event.y)
        }
        invalidate() // Ekranı yenile (çizimi göstermek için)
        return true
    }

    fun getSignatureBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Arka planı saydam yapma
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        draw(canvas)
        return bitmap
    }


    fun clearSignature() {// İmzayı sıfırlamak için
        path.reset()
        invalidate()
    }
}
