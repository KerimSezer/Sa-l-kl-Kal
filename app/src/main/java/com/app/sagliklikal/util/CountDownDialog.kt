package com.app.sagliklikal.util

import android.app.Dialog
import android.content.Context
import android.os.CountDownTimer
import android.widget.TextView
import com.app.sagliklikal.R

// CountDownDialog adında bir sınıf tanımlanıyor
class CountDownDialog(context: Context) {

    // Dialog nesnesi oluşturuluyor ve context üzerinden başlatılıyor
    private var countDownDialog =  Dialog(context)

    // Geri sayım dialog penceresini gösteren fonksiyon
    fun showCountDownDialog(millisecond: Long, callback:(Boolean) -> Unit) {
        // Custom_countdown layoutunu dialog içeriği olarak ayarla
        countDownDialog.setContentView(R.layout.custom_countdown)
        // Dialogun dışına tıklanarak kapatılmasını engelle
        countDownDialog.setCancelable(false)
        // Dialogun arkaplanını transparan olarak ayarla
        countDownDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Geri sayım metnini gösteren TextView bileşenini al
        val countdownText = countDownDialog.findViewById<TextView>(R.id.countdownText)

        // CountDownTimer nesnesi oluştur
        val countDownTimer = object : CountDownTimer(millisecond, 1000) {
            // Her saniye azalarak geri sayım metnini güncelle
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countdownText.text = secondsLeft.toString()
            }

            // Geri sayım tamamlandığında dialogu kapat ve callback fonksiyonunu çağır
            override fun onFinish() {
                countDownDialog.dismiss()
                callback(true)
            }
        }

        // Geri sayımı başlat
        countDownTimer.start()
        // Dialog penceresini göster
        countDownDialog.show()
    }
}
