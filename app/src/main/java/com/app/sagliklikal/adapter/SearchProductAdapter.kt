package com.app.sagliklikal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.sagliklikal.R
import com.app.sagliklikal.model.ProductModel

// Ürünleri arama sonuçları şeklinde gösteren RecyclerView için adaptör sınıfı
class SearchProductAdapter(
    private var productList: List<ProductModel>
) : RecyclerView.Adapter<SearchProductAdapter.ProductViewHolder>() {

    // Seçili ürünleri tutan küme
    private val selectedProducts: MutableSet<ProductModel> = mutableSetOf()

    // Ürün seçildiğinde bildirim yapacak olan dinleyici
    var listener: OnProductSelectedListener? = null

    // Ürün seçildiğinde bildirim yapacak olan arayüz
    interface OnProductSelectedListener {
        fun onProductSelected(product: MutableSet<ProductModel>)
    }

    // View tutucu sınıfı
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productAmount: TextView = itemView.findViewById(R.id.productAmount)
        val productKcal: TextView = itemView.findViewById(R.id.productKcal)
        val productCheckBox: CheckBox = itemView.findViewById(R.id.productCheckBox)
    }

    // Yeni bir ViewHolder oluşturulduğunda çağrılır
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        // Yeni bir View oluştur ve ViewHolder'a bağla
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_search, parent, false)
        return ProductViewHolder(view)
    }

    // ViewHolder'ın bağlandığı pozisyon için veri bağlama işlemi
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        // Liste pozisyonundaki ürünü al
        val product = productList[position]
        // View üzerindeki öğeleri doldur
        holder.productName.text = product.product
        holder.productAmount.text = product.amount
        holder.productKcal.text = "${product.calories} kcal"

        // CheckBox'ın durumunu güncelle
        holder.productCheckBox.setOnCheckedChangeListener(null) // Önce listener'ı kaldır
        holder.productCheckBox.isChecked = selectedProducts.contains(product) // Seçili mi kontrol et

        // CheckBox durum değişikliği dinleyicisi
        holder.productCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedProducts.add(product) // Seçili ürünlere ekle
            } else {
                selectedProducts.remove(product) // Seçili ürünlerden çıkar
            }
            listener?.onProductSelected(selectedProducts) // Dinleyiciye seçili ürünleri bildir
        }
    }

    // Adaptörün tuttuğu öğe sayısını döndürür
    override fun getItemCount(): Int {
        return productList.size
    }

    // Listeyi güncelleyen fonksiyon
    fun updateList(newList: List<ProductModel>) {
        productList = newList
        notifyDataSetChanged() // Veri değişikliğini RecyclerView'e bildir
    }
}
