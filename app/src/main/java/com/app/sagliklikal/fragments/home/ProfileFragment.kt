package com.app.sagliklikal.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.FragmentProfileBinding
import com.app.sagliklikal.model.UpdateProfile
import com.app.sagliklikal.util.CustomProgressDialog
import com.app.sagliklikal.view.AuthActivity
import com.app.sagliklikal.viewmodel.AuthViewModel
import com.app.sagliklikal.viewmodel.DailyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment() {

    // Kullanılacak değişkenlerin tanımlanması
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var binding: FragmentProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val dailyViewModel: DailyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ViewBinding kullanarak fragment'ın layout'unu bağla
        binding = FragmentProfileBinding.inflate(layoutInflater)

        // Alt navigasyon çubuğunu görünür yap
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        // Özel ProgressDialog'u fragment'ın Context'ine göre oluştur ve göster
        customProgressDialog = CustomProgressDialog(requireContext())
        customProgressDialog.show()

        // Kullanıcı verilerini yüklemek için ViewModel üzerinden getUserData çağır
        dailyViewModel.getUserData()

        // userInfo LiveData'sını gözlemle ve kullanıcı bilgilerini ayarla
        dailyViewModel.userInfo.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                // ProgressDialog'u kapat
                customProgressDialog.dismiss()

                // Kullanıcı bilgileri layoutunu görünür yap ve bilgileri göster
                binding.rootLinearForProfile.visibility = View.VISIBLE
                binding.usernameText.text = it.username
                binding.userEmailText.text = it.email
                binding.agetText.text = it.age
                binding.targetWeightText.text = it.height
                binding.weightDifferencesText.text = it.weight

                // Profil güncelleme için kullanılacak veri modelini oluştur
                val updateProfile = UpdateProfile(
                    username = it.username,
                    age = it.age,
                    height = it.height,
                    weight = it.weight,
                    calorieGoal = it.calorieGoal,
                    goal = it.goal,
                    activityLevel = it.activityLevel
                )

                // Profil düzenleme butonuna tıklama işlevselliği ekle
                binding.editProfileLayout.setOnClickListener {
                    val action = ProfileFragmentDirections.actionFragmentProfileToUpdateProfileFragment(updateProfile)
                    findNavController().navigate(action)
                }
            }
        })

        // Çıkış yapma butonuna tıklama işlevselliği ekle
        binding.logoutLayout.setOnClickListener {
            authViewModel.logout() // Oturumu kapatma işlemini ViewModel üzerinden gerçekleştir
            // Oturum kapatıldıktan sonra giriş ekranına yönlendirme yap
            val intent = Intent(requireActivity(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish() // Aktiviteyi kapat
        }

        // Fragment'ın root view'ını döndür
        return binding.root
    }
}
