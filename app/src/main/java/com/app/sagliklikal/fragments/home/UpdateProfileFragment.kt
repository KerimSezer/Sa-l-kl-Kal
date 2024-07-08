package com.app.sagliklikal.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.FragmentUpdateProfileBinding
import com.app.sagliklikal.model.UpdateProfile
import com.app.sagliklikal.util.CustomProgressDialog
import com.app.sagliklikal.viewmodel.DailyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class UpdateProfileFragment : Fragment() {

    // Navigation argümanlarını almak için gerekli değişken
    private val args: UpdateProfileFragmentArgs by navArgs()
    // ViewBinding için gerekli değişken
    private lateinit var binding: FragmentUpdateProfileBinding
    // ViewModel kullanımı için gerekli değişken
    private val dailyViewModel: DailyViewModel by viewModels()
    // ProgressDialog için gerekli değişken
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ViewBinding kullanarak fragment'ın layout'unu bağla
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)

        // Alt navigasyon çubuğunu gizle
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE

        // ProgressDialog'u fragment'ın Context'ine göre oluştur
        progressDialog = CustomProgressDialog(requireContext())

        // Navigation argümanlarından gelen kullanıcı verisini al
        val userData = args.userProfileData

        // EditText alanlarına mevcut kullanıcı verisini yerleştir
        with(binding) {
            usernameText.setText(userData.username)
            ageText.setText(userData.age)
            heightText.setText(userData.height)
            weightText.setText(userData.weight)
            energyConsumptionText.setText(userData.activityLevel)
            goalText.setText(userData.goal)
        }

        // Spinner adapter'ları oluştur ve bağla
        val goals = arrayOf("Kilo Koruma", "Kilo Kaybı", "Kilo Alımı")
        val activityLevels = arrayOf(
            "Sedanter (az hareketli)",
            "Hafif aktif (hafif egzersiz/sportif aktivite 1-3 gün/hafta)",
            "Orta derecede aktif (orta derecede egzersiz/sportif aktivite 3-5 gün/hafta)",
            "Çok aktif (yoğun egzersiz/sportif aktivite 6-7 gün/hafta)",
            "Aşırı aktif (çok yoğun egzersiz, fiziksel iş)"
        )
        val goalsAdapter = ArrayAdapter(binding.root.context, android.R.layout.simple_dropdown_item_1line, goals)
        val energyConsumptionAdapter = ArrayAdapter(binding.root.context, android.R.layout.simple_dropdown_item_1line, activityLevels)

        binding.goalText.setAdapter(goalsAdapter)
        binding.energyConsumptionText.setAdapter(energyConsumptionAdapter)

        // Kaydet butonuna tıklama işlevselliği ekle
        binding.updateButton.setOnClickListener {
            if (isInputValid()) { // Girdi geçerliliğini kontrol et
                // Güncellenecek profil bilgilerini oluştur
                val updatedProfile = UpdateProfile(
                    username = binding.usernameText.text.toString(),
                    age = binding.ageText.text.toString(),
                    height = binding.heightText.text.toString(),
                    weight = binding.weightText.text.toString(),
                    calorieGoal = userData.calorieGoal,
                    goal = binding.goalText.text.toString(),
                    activityLevel = binding.energyConsumptionText.text.toString()
                )

                // ProgressDialog'u göster
                progressDialog.show()

                // ViewModel üzerinden kullanıcı profili güncelleme işlemini gerçekleştir
                dailyViewModel.updateUserProfile(updatedProfile) { success ->
                    progressDialog.dismiss() // ProgressDialog'u kapat
                    // Başarılı veya başarısız duruma göre kullanıcı bilgilendirme yap
                    if (success) {
                        Toast.makeText(context, "Profil başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Profil güncellenemedi!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            }
        }

        // Fragment'ın root view'ını döndür
        return binding.root
    }

    // Girdi geçerliliğini kontrol eden yardımcı metod
    private fun isInputValid(): Boolean {
        return binding.usernameText.text!!.isNotBlank() &&
                binding.ageText.text!!.isNotBlank() &&
                binding.heightText.text!!.isNotBlank() &&
                binding.weightText.text!!.isNotBlank() &&
                binding.goalText.text!!.isNotBlank() &&
                binding.energyConsumptionText.text.isNotBlank()
    }
}
