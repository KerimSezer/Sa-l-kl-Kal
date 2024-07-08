package com.app.sagliklikal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

// Kullanıcı kimlik doğrulama ve kayıt işlemlerini yöneten AuthViewModel sınıfı
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance() // Firebase Authentication instance
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance() // Firebase Firestore instance

    // Kullanıcı kaydı durumunu takip eden LiveData
    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> get() = _registrationState

    // Kullanıcı giriş durumunu takip eden LiveData
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    // Kullanıcı kaydı işlemi
    fun registerUser(username: String, email: String, password: String, age: Int, weight: Double, height: Int, gender: String, activityLevel: String, goal: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firestore'a kullanıcı bilgilerini kaydetme
                    saveUserToFirestore(username, email, age, weight, height, gender, activityLevel, goal)
                } else {
                    // Başarısız kayıt durumu
                    _registrationState.value = RegistrationState.Failure(task.exception?.message ?: "Bilinmeyen hata")
                }
            }
    }

    // Firestore'a kullanıcı bilgilerini kaydetme
    private fun saveUserToFirestore(username: String, email: String, age: Int, weight: Double, height: Int, gender: String, activityLevel: String, goal: String) {
        val bmr = calculateBMR(gender, weight, height, age) // BMR hesaplama
        val tdee = calculateTDEE(bmr, activityLevel) // TDEE hesaplama
        val calorieGoal = calculateCalorieGoal(tdee.toInt(), goal) // Kalori hedefi hesaplama

        // Firestore'a kaydedilecek kullanıcı verileri
        val user = hashMapOf(
            "username" to username,
            "email" to email,
            "age" to age,
            "weight" to weight,
            "height" to height,
            "gender" to gender,
            "activityLevel" to activityLevel,
            "calorieGoal" to calorieGoal,
            "goal" to goal
        )

        // Firestore'a belirtilen koleksiyona belirtilen doküman adı ile kullanıcı verilerini kaydetme
        firestore.collection("users").document(email).set(user)
            .addOnSuccessListener {
                // Başarılı kayıt durumu
                _registrationState.value = RegistrationState.Success
            }
            .addOnFailureListener { e ->
                // Başarısız kayıt durumu
                _registrationState.value = RegistrationState.Failure(e.message ?: "Bilinmeyen hata")
            }
    }

    // Kullanıcı giriş işlemi
    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Başarılı giriş durumu
                    _loginState.value = LoginState.Success
                } else {
                    // Başarısız giriş durumu
                    _loginState.value = LoginState.Failure(task.exception?.message ?: "Bilinmeyen hata")
                }
            }
    }

    // Mevcut kullanıcıyı döndürme
    fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Kullanıcı oturumu kapatma
    fun logout() = auth.signOut()

    // BMR hesaplama
    private fun calculateBMR(gender: String, weight: Double, height: Int, age: Int): Double {
        return if (gender.toLowerCase(Locale.ROOT) == "erkek") {
            88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        } else {
            447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        }
    }

    // TDEE hesaplama
    private fun calculateTDEE(bmr: Double, activityLevel: String): Double {
        return when (activityLevel.toLowerCase(Locale.ROOT)) {
            "sedanter (az hareketli)" -> bmr * 1.2
            "hafif aktif (hafif egzersiz/sportif aktivite 1-3 gün/hafta)" -> bmr * 1.375
            "orta derecede aktif (orta derecede egzersiz/sportif aktivite 3-5 gün/hafta)" -> bmr * 1.55
            "çok aktif (yoğun egzersiz/sportif aktivite 6-7 gün/hafta)" -> bmr * 1.725
            "aşırı aktif (çok yoğun egzersiz, fiziksel iş)" -> bmr * 1.9
            else -> bmr * 1.2 // Varsayılan olarak sedanter seviye
        }
    }

    // Kalori hedefi hesaplama
    private fun calculateCalorieGoal(tdee: Int, goal: String): Int {
        return when (goal.toLowerCase(Locale.ROOT)) {
            "kilo koruma" -> tdee
            "kilo kaybı" -> (tdee - 500) // Günlük 500 kalori azaltma
            "kilo alımı" -> (tdee + 500) // Günlük 500 kalori artırma
            else -> tdee
        }
    }

    // Kullanıcı kayıt durumunu temsil eden sealed class
    sealed class RegistrationState {
        object Success : RegistrationState()
        data class Failure(val errorMessage: String) : RegistrationState()
    }

    // Kullanıcı giriş durumunu temsil eden sealed class
    sealed class LoginState {
        object Success : LoginState()
        data class Failure(val errorMessage: String) : LoginState()
    }
}
