package com.app.sagliklikal.fragments.exercise

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.sagliklikal.R
import com.app.sagliklikal.adapter.MuscleGroupsAdapter
import com.app.sagliklikal.databinding.FragmentExerciseBinding
import com.app.sagliklikal.model.MuscleGroupModel
import com.app.sagliklikal.util.CustomProgressDialog
import com.app.sagliklikal.util.TrainingType
import com.app.sagliklikal.viewmodel.DailyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

/**
 * Egzersizleri listelemek için kullanılan Fragment sınıfı.
 */
class ExerciseFragment : Fragment() {

    private lateinit var dailyViewModel: DailyViewModel
    private lateinit var binding: FragmentExerciseBinding
    private lateinit var adapter: MuscleGroupsAdapter
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // FragmentExerciseBinding kullanarak view bağlaması yapılıyor
        binding = FragmentExerciseBinding.inflate(layoutInflater)
        // BottomNavigationView'u görünür yap
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        // CustomProgressDialog başlatılıyor
        progressDialog = CustomProgressDialog(requireContext())

        // ViewModelProvider ile DailyViewModel oluşturuluyor
        dailyViewModel = ViewModelProvider(this)[DailyViewModel::class.java]
        // Kullanıcı verilerini getirme işlemi başlatılıyor
        dailyViewModel.getUserData()

        // İlerleme dialogunu göster
        progressDialog.show()

        // Observer ile kullanıcı bilgilerinin değişimini dinle
        dailyViewModel.userInfo.observe(viewLifecycleOwner, Observer { userInfo ->
            if (userInfo != null) {
                val userGoal = userInfo.goal
                // Kullanıcının hedefine göre başlık metnini güncelle
                binding.goalTrainingText.text = "$userGoal - Antrenman Programı"
                // assets içindeki exercises.json dosyasından JSON verisi okunuyor
                val jsonObject = readJsonFromAssets("exercises.json", requireContext())
                val jsonObjectName = when (userGoal) {
                    // Kullanıcı hedefine göre JSON'dan ilgili objeyi seç
                    TrainingType.WEIGHTGAIN.description -> "weightGain"
                    TrainingType.WEIGHTLOSS.description -> "weightLoss"
                    TrainingType.WEIGHTMAINTAINING.description -> "weightMaintaining"
                    else -> null
                }

                // JSON objesi ve adı null değilse işleme devam et
                if (jsonObject != null && jsonObjectName != null) {
                    val weightObject = jsonObject.getJSONObject(jsonObjectName)
                    val keys = weightObject.keys()
                    val exerciseInfoList = ArrayList<MuscleGroupModel>()

                    // JSON'dan gelen her bir anahtar için işlem yap
                    keys.forEach { key ->
                        val exerciseObject = weightObject.getJSONObject(key)
                        val imageURL = exerciseObject.getString("imageURL").toString()
                        val bodyPart = exerciseObject.getString("bodyPart")
                        // MuscleGroupModel oluştur ve listeye ekle
                        val muscleGroup = MuscleGroupModel(key , userGoal, bodyPart, imageURL)
                        exerciseInfoList.add(muscleGroup)
                    }

                    // MuscleGroupsAdapter ile RecyclerView için adapter oluştur
                    adapter = MuscleGroupsAdapter(exerciseInfoList)
                    // RecyclerView'e adapter'i bağla
                    binding.muscleGroupsRecycler.adapter = adapter
                } else {
                    // JSON objesi veya adı null ise hata kaydı oluştur
                    Log.e("ExerciseFragment", "JSON object or name is null")
                }
            }

            // Veri yüklendikten sonra ilerleme dialogunu kapat
            progressDialog.dismiss()
        })

        // Fragment'in root view'ını döndür
        return binding.root
    }

    // assets içindeki JSON dosyasını okuyan fonksiyon
    private fun readJsonFromAssets(fileName: String, context: Context): JSONObject? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            JSONObject(String(buffer, Charsets.UTF_8))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
