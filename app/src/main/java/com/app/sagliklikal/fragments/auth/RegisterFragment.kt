package com.app.sagliklikal.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.sagliklikal.databinding.FragmentRegisterBinding
import com.app.sagliklikal.viewmodel.AuthViewModel

// Kullanıcı kaydı yapılmasını sağlayan Fragment sınıfı
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: AuthViewModel

    // Fragment oluşturulduğunda çağrılır
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View bağlaması yap
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        // ViewModel'ı başlat
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Hedefler, cinsiyetler ve aktivite seviyeleri için adapter'lar oluştur
        val goals = arrayOf("Kilo Koruma", "Kilo Kaybı", "Kilo Alımı")
        val genders = arrayOf("Erkek", "Kadın")
        val activityLevels = arrayOf(
            "Sedanter (az hareketli)",
            "Hafif aktif (hafif egzersiz/sportif aktivite 1-3 gün/hafta)",
            "Orta derecede aktif (orta derecede egzersiz/sportif aktivite 3-5 gün/hafta)",
            "Çok aktif (yoğun egzersiz/sportif aktivite 6-7 gün/hafta)",
            "Aşırı aktif (çok yoğun egzersiz, fiziksel iş)"
        )
        val goalsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, goals)
        val genderAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genders)
        val energyConsumptionAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                activityLevels
            )

        // Adapter'ları ilgili AutoCompleteTextView'lere bağla
        binding.goalText.setAdapter(goalsAdapter)
        binding.genderText.setAdapter(genderAdapter)
        binding.energyConsumptionText.setAdapter(energyConsumptionAdapter)

        // "Zaten hesabınız var mı?" butonuna tıklama olayı
        binding.haveAccountBt.setOnClickListener {
            findNavController().navigate(com.app.sagliklikal.R.id.action_registerFragment_to_loginFragment)
        }

        // "Hesap Oluştur" butonuna tıklama olayı
        binding.createButton.setOnClickListener {
            val username = binding.usernameText.text.toString().trim()
            val email = binding.emailText.text.toString().trim()
            val password = binding.passwordText.text.toString().trim()
            val age = binding.ageText.text.toString().toIntOrNull() ?: 0
            val weight = binding.weightText.text.toString().toDoubleOrNull() ?: 0.0
            val height = binding.heightText.text.toString().toIntOrNull() ?: 0
            val gender = binding.genderText.text.toString()
            val activityLevel = binding.energyConsumptionText.text.toString()
            val goal = binding.goalText.text.toString()

            // Gerekli alanların boş olmadığından emin ol
            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() &&
                age.toString().isNotEmpty() && weight.toString().isNotEmpty() && height.toString()
                    .isNotEmpty() && gender.isNotEmpty()
                && activityLevel.isNotEmpty() && goal.isNotEmpty()
            ) {
                binding.signupProgress.visibility = View.VISIBLE
                // ViewModel üzerinden kullanıcı kaydını yap
                viewModel.registerUser(username, email, password, age, weight, height, gender, activityLevel, goal)
            } else {
                // Boş alan varsa hata göster
                Toast.makeText(requireContext(), "Boş alanları doldurunuz.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // ViewModel'dan kayıt durumunu izle
        viewModel.registrationState.observe(viewLifecycleOwner) { state ->
            binding.signupProgress.visibility = View.GONE // İlerleme çubuğunu gizle
            when (state) {
                is AuthViewModel.RegistrationState.Success -> {
                    // Kayıt başarılı ise kullanıcıya mesaj göster ve giriş ekranına yönlendir
                    Toast.makeText(
                        requireContext(),
                        "Kullanıcı başarıyla kaydoldu!",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(com.app.sagliklikal.R.id.action_registerFragment_to_loginFragment)
                }
                is AuthViewModel.RegistrationState.Failure -> {
                    // Kayıt başarısız ise hata mesajı göster
                    Toast.makeText(
                        requireContext(),
                        "Kayıt başarısız: ${state.errorMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return binding.root // View'i döndür
    }
}
