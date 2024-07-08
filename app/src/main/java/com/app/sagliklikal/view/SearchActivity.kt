package com.app.sagliklikal.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.sagliklikal.adapter.SearchProductAdapter
import com.app.sagliklikal.databinding.ActivitySearchBinding
import com.app.sagliklikal.model.ConsumedCalories
import com.app.sagliklikal.model.ProductModel
import com.app.sagliklikal.util.CustomProgressDialog
import com.app.sagliklikal.util.DateTimeUtil
import com.app.sagliklikal.viewmodel.DailyViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.Charset

// Ürün arama ve seçim işlevselliği sunan SearchActivity sınıfı
class SearchActivity : AppCompatActivity(), SearchProductAdapter.OnProductSelectedListener {

    private lateinit var customProgress: CustomProgressDialog
    private lateinit var dailyViewModel: DailyViewModel
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchProductAdapter: SearchProductAdapter
    private var selectedProducts: ConsumedCalories = ConsumedCalories(null, listOf())
    private var productList: List<ProductModel> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customProgress = CustomProgressDialog(this@SearchActivity)
        dailyViewModel = ViewModelProvider(this)[DailyViewModel::class.java]

        // Intent'ten meal-type bilgisini alarak gösterim yapma
        val partOfDay = intent.getStringExtra("meal-type")
        binding.partOfDay.text = partOfDay
        binding.todayDate.text = DateTimeUtil.getCurrentDate()

        // RecyclerView'in başlatılması
        val layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            binding.productListRecycler.context,
            layoutManager.orientation
        )
        binding.productListRecycler.layoutManager = layoutManager
        searchProductAdapter = SearchProductAdapter(productList)
        binding.productListRecycler.adapter = searchProductAdapter
        binding.productListRecycler.addItemDecoration(dividerItemDecoration)

        // Ürünlerin JSON'dan yüklenmesi
        loadProductsFromJSON()

        // CheckIcon'a tıklama işlevselliği
        binding.checkIcon.setOnClickListener {
            customProgress.show()
            selectedProducts.partOfData = partOfDay
            dailyViewModel.saveConsumedCalories(selectedProducts) { result ->
                customProgress.dismiss()
                if (result) finish()
            }
        }

        binding.closeIcon.setOnClickListener {
            finish()
        }

        // SearchView'e metin girildiğinde veya gönderildiğinde arama işlevselliği
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query ?: "")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText ?: "")
                return false
            }
        })

        // Adapter'a listener atanması
        searchProductAdapter.listener = this
    }

    // JSON'dan ürünlerin yüklenmesi
    private fun loadProductsFromJSON() {
        val jsonFileString = getJsonDataFromAsset()
        if (jsonFileString != null) {
            val gson = Gson()
            val listProductType = object : TypeToken<List<ProductModel>>() {}.type
            productList = gson.fromJson(jsonFileString, listProductType)
            searchProductAdapter.updateList(productList)
        }
    }

    // Assets klasöründen JSON verisi alma
    private fun getJsonDataFromAsset(): String? {
        return try {
            val inputStream = assets.open("product.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    // Ürünlerin filtrelenmesi ve RecyclerView'in güncellenmesi
    private fun filterProducts(query: String) {
        val filteredList = productList.filter {
            it.product.contains(query, ignoreCase = true)
        }
        searchProductAdapter.updateList(filteredList)
    }

    // Ürün seçildiğinde çağrılan fonksiyon
    override fun onProductSelected(product: MutableSet<ProductModel>) {
        if (product.isNotEmpty()) {
            selectedProducts.products = product.toList()
            binding.amountText.apply {
                visibility = View.VISIBLE
                text = product.size.toString()
            }
            binding.checkIcon.visibility = View.VISIBLE
        } else {
            selectedProducts.products = listOf()
            binding.amountText.visibility = View.GONE
            binding.checkIcon.visibility = View.GONE
        }
    }
}
