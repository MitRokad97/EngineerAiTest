package com.test.engineerai.customui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView


class CircularImageView(context: Context, attributeSet: AttributeSet) :
    ImageView(context, attributeSet) {

    override fun onDraw(canvas: Canvas) {

        val drawable = getDrawable() ?: return

        if (getWidth() == 0 || getHeight() == 0) {
            return
        }
        val b = (drawable as BitmapDrawable).bitmap
        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)

        val w = getWidth()
        val h = getHeight()

        val roundBitmap = getRoundedCroppedBitmap(bitmap, w)
        canvas.drawBitmap(roundBitmap, 0f, 0f, null)

    }

    companion object {

        fun getRoundedCroppedBitmap(bitmap: Bitmap, radius: Int): Bitmap {
            val finalBitmap: Bitmap
            if (bitmap.width != radius || bitmap.height != radius)
                finalBitmap = Bitmap.createScaledBitmap(
                    bitmap, radius, radius,
                    false
                )
            else
                finalBitmap = bitmap
            val output = Bitmap.createBitmap(
                finalBitmap.width,
                finalBitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)

            val paint = Paint()
            val rect = Rect(
                0, 0, finalBitmap.width,
                finalBitmap.height
            )

            paint.setAntiAlias(true)
            paint.setFilterBitmap(true)
            paint.setDither(true)
            canvas.drawARGB(0, 0, 0, 0)
            paint.setColor(Color.parseColor("#BAB399"))
            canvas.drawCircle(
                finalBitmap.width / 2 + 0.7f,
                finalBitmap.height / 2 + 0.7f,
                finalBitmap.width / 2 + 0.1f, paint
            )
            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
            canvas.drawBitmap(finalBitmap, rect, rect, paint)

            return output
        }
    }
}