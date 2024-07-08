package com.app.sagliklikal.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// DateTimeUtil adında bir yardımcı obje tanımlanıyor
object DateTimeUtil {

    // Geçerli tarihi formatlı bir string olarak döndüren fonksiyon
    fun getCurrentDate() : String {
        // Bir Calendar nesnesi oluşturuluyor ve geçerli tarih alınıyor
        val calendar = Calendar.getInstance()
        // SimpleDateFormat ile tarih formatı belirleniyor ("E, MMM d" formatı Türkçe)
        val dateFormat = SimpleDateFormat("E, MMM d", Locale("tr"))
        // Tarih formatlandıktan sonra string olarak döndürülüyor
        return dateFormat.format(calendar.time)
    }
}
