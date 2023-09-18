package com.swu.dimiz.ogg.oggdata

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

//@ProvidedTypeConverter
class RoomTypeConverter {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray {

        val outputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}