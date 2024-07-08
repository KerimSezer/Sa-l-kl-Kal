package com.app.sagliklikal.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.app.sagliklikal.R
import com.bumptech.glide.Glide


// ImageView için genişletme işlevi: URL'den resim yükleme
fun ImageView.downloadImageFromURL(url: String) {
    // Glide kütüphanesi ile resim yükleniyor
    Glide.with(context)
        .asBitmap() // Resim olarak yükleme
        .load(url) // Belirtilen URL'den yükleme yap
        .centerCrop() // Resmi merkeze hizala ve kırp
        .placeholder(R.drawable.glide_place_animation) // Yükleme sırasında yer tutucu animasyon göster
        .error(R.drawable.ic_launcher_background) // Yükleme hatası durumunda hata resmi göster
        .into(this) // ImageView'e yüklenen resmi yerleştir
}

// ImageView için genişletme işlevi: URL'den GIF yükleme
fun ImageView.downloadGifFromURL(url: String) {
    // Glide kütüphanesi ile GIF yükleniyor
    Glide.with(context)
        .asGif() // GIF olarak yükleme
        .load(url) // Belirtilen URL'den yükleme yap
        .centerCrop() // GIF'i merkeze hizala ve kırp
        .placeholder(R.drawable.glide_place_animation) // Yükleme sırasında yer tutucu animasyon göster
        .error(R.drawable.ic_launcher_background) // Yükleme hatası durumunda hata resmi göster
        .into(this) // ImageView'e yüklenen GIF'i yerleştir
}

// Veri bağlama için BindingAdapter: XML'de android:getImage etiketiyle belirtilen URL'den görüntü yükleme
@BindingAdapter("android:getImage")
fun getImage(view: ImageView, imageURL: String) {
    // downloadImageFromURL işlevini çağırarak görüntü yükleme işlemi gerçekleştiriliyor
    view.downloadImageFromURL(imageURL)
}
