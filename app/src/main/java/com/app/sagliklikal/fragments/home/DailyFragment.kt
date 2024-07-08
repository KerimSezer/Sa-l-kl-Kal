package com.app.sagliklikal.fragments.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.sagliklikal.R
import com.app.sagliklikal.adapter.BreakfastAdapter
import com.app.sagliklikal.adapter.DinnerAdapter
import com.app.sagliklikal.adapter.LaunchAdapter
import com.app.sagliklikal.adapter.OtherAdapter
import com.app.sagliklikal.databinding.FragmentDailyBinding
import com.app.sagliklikal.model.ProductModel
import com.app.sagliklikal.util.CustomProgressDialog
import com.app.sagliklikal.util.MealType
import com.app.sagliklikal.view.SearchActivity
import com.app.sagliklikal.viewmodel.DailyViewModel

class DailyFragment : Fragment() {

    // Kullanıcı hedef kalori bilgisini tutacak değişken
    private var userCalorieGoal: String? = null

    // ViewModel ve ViewBinding için gerekli değişkenler
    private lateinit var dailyViewModel: DailyViewModel
    private lateinit var binding: FragmentDailyBinding

    // İlerleme göstergesi için özel ProgressDialog
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ViewBinding'i kullanarak fragment'ın layoutunu bağla
        binding = FragmentDailyBinding.inflate(layoutInflater)

        // ProgressDialog'u fragment'ın Context'ine göre oluştur. Yükleniyor
        progressDialog = CustomProgressDialog(requireContext())

        // ViewModelProvider aracılığıyla DailyViewModel'i başlat
        dailyViewModel = ViewModelProvider(this)[DailyViewModel::class.java]

        // ProgressDialog'u göster
        progressDialog.show()

        // Kullanıcı verilerini ViewModel üzerinden alma ve gözlemleme
        dailyViewModel.getUserData()
        dailyViewModel.userInfo.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                // Kullanıcı adını ve hedef kaloriyi görüntüye ayarla
                userCalorieGoal = user.calorieGoal
                binding.helloTxt.text = "Merhaba, ${user.username} \uD83D\uDC4B"
                binding.goalCalorie.text = userCalorieGoal

                // Tüketilen kalorileri almak için ViewModel metodu çağır
                dailyViewModel.getConsumedCalories()
            }
        })

        // Tüketilen kalorileri gözlemleme ve RecyclerView'leri ayarlama
        dailyViewModel.consumedCaloriesLiveData.observe(
            viewLifecycleOwner,
            Observer { consumedCaloriesList ->
                // ProgressDialog'u kapat
                progressDialog.dismiss()

                // RecyclerView ve diğer görünümleri görünür yap
                binding.dailyRootLinear.visibility = View.VISIBLE

                // Tüketilen kalori listesi null değilse işle
                consumedCaloriesList?.let { list ->
                    // Kahvaltı verilerini filtrele ve RecyclerView için ayarla
                    val filteredBreakfastList = list.filter { it.partOfData == MealType.BREAKFAST.description }
                    val breakfastProducts = filteredBreakfastList.flatMap { it.products }
                    val breakfastCalories = setupBreakfastRecyclerView(breakfastProducts)

                    // Öğle yemeği verilerini filtrele ve RecyclerView için ayarla
                    val filteredLaunchList = list.filter { it.partOfData == MealType.LAUNCH.description }
                    val launchProducts = filteredLaunchList.flatMap { it.products }
                    val launchCalories = setupLaunchRecyclerView(launchProducts)

                    // Akşam yemeği verilerini filtrele ve RecyclerView için ayarla
                    val filteredDinnerList = list.filter { it.partOfData == MealType.DINNER.description }
                    val dinnerProducts = filteredDinnerList.flatMap { it.products }
                    val dinnerCalories = setupDinnerRecyclerView(dinnerProducts)

                    // Diğer verilerini filtrele ve RecyclerView için ayarla
                    val filteredOtherList = list.filter { it.partOfData == MealType.OTHER.description }
                    val otherProducts = filteredOtherList.flatMap { it.products }
                    val otherCalories = setupOtherRecyclerView(otherProducts)

                    // Toplam tüketilen kalorileri hesapla ve görünüme ayarla
                    val totalCalories = breakfastCalories + launchCalories + dinnerCalories + otherCalories
                    binding.intakeTotalCalories.text = totalCalories.toString()

                    // Hedefe göre maksimum ve mevcut ilerleme çubuğunu ayarla
                    binding.circularSeekBar.max = (userCalorieGoal?.toInt() ?: 2500).toFloat()
                    binding.circularSeekBar.progress = totalCalories.toFloat()

                    // İlerleme çubuğunun rengini hedefe göre ayarla
                    val seekBarColor = if (totalCalories.toFloat() > userCalorieGoal?.toFloat()!!) {
                        ContextCompat.getColor(requireContext(), R.color.red)
                    } else {
                        ContextCompat.getColor(requireContext(), R.color.green)
                    }
                    binding.circularSeekBar.circleProgressColor = seekBarColor
                }
            }
        )

        // Butonlara tıklama işlevselliği ekleme
        with(binding) {
            addToBreakfast.setOnClickListener {
                intentToSearch(requireContext(), MealType.BREAKFAST)
            }
            addToLaunch.setOnClickListener {
                intentToSearch(requireContext(), MealType.LAUNCH)
            }
            addToDinner.setOnClickListener {
                intentToSearch(requireContext(), MealType.DINNER)
            }
            addToAperitif.setOnClickListener {
                intentToSearch(requireContext(), MealType.OTHER)
            }
            breakfastToggle.setOnClickListener {
                toggleLayoutVisibility(breakfastItemsLayout, breakfastToggle)
            }
            launchToggle.setOnClickListener {
                toggleLayoutVisibility(launchItemsLayout, launchToggle)
            }
            dinnerToggle.setOnClickListener {
                toggleLayoutVisibility(dinnerItemsLayout, dinnerToggle)
            }
            otherToggle.setOnClickListener {
                toggleLayoutVisibility(otherItemsLayout, otherToggle)
            }
        }

        // Fragment'ın root view'ını döndür
        return binding.root
    }

    // Kahvaltı RecyclerView'ini ayarla ve toplam kalorileri döndür
    private fun setupBreakfastRecyclerView(breakfastItems: List<ProductModel?>): Int {
        val totalCalories = breakfastItems.filterNotNull().sumOf { it.calories.toInt() }
        binding.breakfastKcal.text = "$totalCalories Kalori"
        binding.breakfastAmount.text = "${breakfastItems.size} ürün"
        binding.breakfastRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.breakfastRecycler.adapter = BreakfastAdapter(breakfastItems)
        return totalCalories
    }

    // Öğle yemeği RecyclerView'ini ayarla ve toplam kalorileri döndür
    private fun setupLaunchRecyclerView(launchItems: List<ProductModel?>): Int {
        val totalCalories = launchItems.filterNotNull().sumOf { it.calories.toInt() }
        binding.launchKcal.text = "$totalCalories Kalori"
        binding.launchAmount.text = "${launchItems.size} ürün"
        binding.launchRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.launchRecycler.adapter = LaunchAdapter(launchItems)
        return totalCalories
    }

    // Akşam yemeği RecyclerView'ini ayarla ve toplam kalorileri döndür
    private fun setupDinnerRecyclerView(dinnerItems: List<ProductModel?>): Int {
        val totalCalories = dinnerItems.filterNotNull().sumOf { it.calories.toInt() }
        binding.dinnerKcal.text = "$totalCalories Kalori"
        binding.dinnerAmount.text = "${dinnerItems.size} ürün"
        binding.dinnerRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.dinnerRecycler.adapter = DinnerAdapter(dinnerItems)
        return totalCalories
    }

    // Diğer öğünler RecyclerView'ini ayarla ve toplam kalorileri döndür
    private fun setupOtherRecyclerView(otherItems: List<ProductModel?>): Int {
        val totalCalories = otherItems.filterNotNull().sumOf { it.calories.toInt() }
        binding.otherKcal.text = "$totalCalories Kalori"
        binding.otherAmount.text = "${otherItems.size} ürün"
        binding.otherRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.otherRecycler.adapter = OtherAdapter(otherItems)
        return totalCalories
    }

    // LinearLayout'in görünürlüğünü toggle etmek için yardımcı fonksiyon
    private fun toggleLayoutVisibility(layout: LinearLayout, toggle: ImageView) {
        if (layout.visibility == View.VISIBLE) {
            layout.visibility = View.GONE
            toggle.setImageResource(R.drawable.baseline_expand_more_24)
        } else {
            layout.visibility = View.VISIBLE
            toggle.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
        }
    }

}

// Arama aktivitesine geçiş için Intent oluşturma
private fun intentToSearch(context: Context, mealType: MealType) {
    val intent = Intent(context, SearchActivity::class.java)
    intent.putExtra("meal-type", mealType.description)
    context.startActivity(intent)
}
