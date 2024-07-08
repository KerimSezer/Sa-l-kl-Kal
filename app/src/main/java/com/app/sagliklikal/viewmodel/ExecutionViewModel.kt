package com.app.sagliklikal.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.sagliklikal.model.ExecutionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

class ExecutionViewModel(private val application: Application) : AndroidViewModel(application) {

    // Firebase Firestore ve Auth referansları
    private val firestoreService = FirebaseFirestore.getInstance()
    private val firebaseAuthService = FirebaseAuth.getInstance()

    // Geçerli kullanıcının e-posta adresi
    private val currentEmail = firebaseAuthService.currentUser!!.email.toString()

    // Firestore'da kullanılacak referans
    private val reference = firestoreService.collection("workout").document(currentEmail)

    // Egzersiz kaydını Firebase Firestore'a kaydedecek fonksiyon
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveWorkout(execution: ExecutionModel, result: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            // Firestore'a yeni egzersiz verisini ekleme işlemi
            reference.collection("workout").add(workoutHash(execution)).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result(true, "Egzersiz Başarıyla Kaydedildi!")
                } else {
                    result(false, "Egzersiz kaydedilirken hata oluştu!")
                }
            }.addOnFailureListener {
                result(false, "Egzersiz kaydedilirken hata oluştu!")
            }
        }
    }

    // Egzersiz verilerini HashMap'e dönüştürecek yardımcı fonksiyon
    @RequiresApi(Build.VERSION_CODES.O)
    private fun workoutHash(execution: ExecutionModel) : HashMap<String, Any> {
        // Egzersiz için benzersiz bir UUID oluşturma
        val uuid = UUID.randomUUID().toString().substring(0, 12)

        // Şu anki zaman bilgisini almak
        val currentTimeStamp = System.currentTimeMillis()

        // Seçilen tarih
        val selectedDate = execution.date

        // Seçilen gün adı (ör. Paz, Pzt)
        val selectedDay = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

        // Egzersiz metni oluşturma
        val todoText = "${execution.exerciseName}, ${execution.weight} ${execution.type}\n${execution.set} SET x ${execution.rep} REP"

        // HashMap oluşturma ve doldurma
        return hashMapOf(
            "selectedDay" to selectedDay,
            "selectedDate" to selectedDate,
            "todoText" to todoText,
            "todoId" to uuid,
            "createdAt" to currentTimeStamp
        )
    }
}
