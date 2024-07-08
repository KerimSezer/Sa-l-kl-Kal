package com.app.sagliklikal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.ItemsProductBinding
import com.app.sagliklikal.model.ProductModel

// Akşam yemeği ürünlerini gösteren RecyclerView için adaptör sınıfı
class DinnerAdapter(private val itemList: List<ProductModel?>) : RecyclerView.Adapter<DinnerAdapter.ViewHolder>() {

    // View tutucu sınıfı
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Yeni bir ViewHolder oluşturulduğunda çağrılır
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Yeni bir View oluştur ve ViewHolder'a bağla
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_product, parent, false)
        return ViewHolder(view)
    }

    // ViewHolder'ın bağlandığı pozisyon için veri bağlama işlemi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Liste öğesini al
        val item = itemList[position]
        // View bağlama işlemi için veri bağlama sınıfını kullan
        val binding = ItemsProductBinding.bind(holder.itemView)
        binding.product = item
    }

    // Adaptörün tuttuğu öğe sayısını döndürür
    override fun getItemCount(): Int {
        return itemList.size
    }
}
