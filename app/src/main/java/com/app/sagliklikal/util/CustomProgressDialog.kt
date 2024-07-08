package com.app.sagliklikal.util

import android.app.Dialog
import android.content.Context
import com.app.sagliklikal.R

// CustomProgressDialog adında bir sınıf tanımlanıyor
class CustomProgressDialog(val context: Context) {

    // Dialog nesnesi oluşturuluyor ve context üzerinden başlatılıyor
    private var progressDialog =  Dialog(context)

    // İlerleme göstergesi dialogunu gösteren fonksiyon
    fun show() {
        // Full_screen_progress_bar layoutunu dialog içeriği olarak ayarla
        progressDialog.setContentView(R.layout.full_screen_progress_bar)
        // Dialogun dışına tıklanarak kapatılmasını engelle
        progressDialog.setCancelable(false)
        // Dialogun arkaplanını transparan olarak ayarla
        progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        // Dialog penceresini göster
        progressDialog.show()
    }

    // İlerleme göstergesi dialogunu kapatmak için kullanılan fonksiyon
    fun dismiss() {
        // Dialog penceresini kapat
        progressDialog.dismiss()
    }
}
