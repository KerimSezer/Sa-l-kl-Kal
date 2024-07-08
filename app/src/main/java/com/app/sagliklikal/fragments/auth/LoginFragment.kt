package com.app.sagliklikal.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.FragmentLoginBinding
import com.app.sagliklikal.view.MainActivity
import com.app.sagliklikal.viewmodel.AuthViewModel

// Kullanıcı girişi sağlayan Fragment sınıfı
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel

    // Fragment oluşturulduğunda çağrılır
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View bağlaması yap
        binding = FragmentLoginBinding.inflate(layoutInflater)
        // ViewModel'ı başlat
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Hesap oluşturma butonu tıklama olayı
        binding.createAccountBT.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Giriş yap butonu tıklama olayı
        binding.signInButton.setOnClickListener {
            val email = binding.emailText.text.toString().trim()
            val password = binding.passwordText.text.toString().trim()

            // E-posta ve şifre boş değilse işlem yap
            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.progressBarSign.visibility = View.VISIBLE
                viewModel.loginUser(email, password) // ViewModel üzerinden kullanıcı girişini yap
            } else {
                // E-posta ve şifre boş ise hata göster
                Toast.makeText(requireContext(), "Lütfen e-posta adresinizi ve şifrenizi girin.", Toast.LENGTH_SHORT).show()
            }
        }

        // ViewModel'dan giriş durumunu izle
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            binding.progressBarSign.visibility = View.GONE // İlerleme çubuğunu gizle
            when (state) {
                is AuthViewModel.LoginState.Success -> {
                    // Giriş başarılı ise ana ekranı aç
                    Toast.makeText(requireContext(), "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is AuthViewModel.LoginState.Failure -> {
                    // Giriş başarısız ise hata göster
                    Toast.makeText(requireContext(), "Giriş Hatalı: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root // View'i döndür
    }
}
