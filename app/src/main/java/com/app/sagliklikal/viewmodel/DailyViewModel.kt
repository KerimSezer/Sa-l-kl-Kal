package com.app.sagliklikal.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.sagliklikal.model.ConsumedCalories
import com.app.sagliklikal.model.ProductModel
import com.app.sagliklikal.model.UpdateProfile
import com.app.sagliklikal.model.User
import com.app.sagliklikal.util.DateTimeUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DailyViewModel : ViewModel() {

    // Kullanıcı bilgilerini tutacak LiveData
    val userInfo = MutableLiveData<User?>()

    // Firebase Firestore ve Auth referansları
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Tüketilen kalorilerin listesini tutacak LiveData
    val consumedCaloriesLiveData = MutableLiveData<List<ConsumedCalories>?>()

    // Firestore koleksiyon yolları ve geçerli tarih
    private val collectionPath = "consumed-calories"
    private val currentDate = DateTimeUtil.getCurrentDate()

    // Kullanıcı verilerini Firestore'dan alacak fonksiyon
    fun getUserData() {
        firestore.collection("users").document(auth.currentUser!!.email.toString()).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firestore'dan alınan verileri kullanıcı nesnesine dönüştürme
                    val user = User(
                        task.result.data!!["username"].toString(),
                        task.result.data!!["email"].toString(),
                        task.result.data!!["age"].toString(),
                        task.result.data!!["gender"].toString(),
                        task.result.data!!["height"].toString(),
                        task.result.data!!["weight"].toString(),
                        task.result.data!!["calorieGoal"].toString(),
                        task.result.data!!["goal"].toString(),
                        task.result.data!!["activityLevel"].toString()
                    )
                    // userInfo LiveData'sını güncelleme
                    userInfo.value = user
                } else userInfo.value = null
            }.addOnFailureListener {
                userInfo.value = null
                Log.d("daily-log", "Kullanıcı Bilgisi Alınamadı: ${it.message}")
            }
    }

    // Tüketilen kalorileri Firestore'dan dinleyecek fonksiyon
    fun getConsumedCalories() {
        firestore.collection(collectionPath).document(auth.currentUser!!.email.toString())
            .collection(currentDate)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore", "Veri dinlemesi başarısız.", e)
                    consumedCaloriesLiveData.value = null
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    // Tüketilen kalori verilerini haritalamak için kullanılacak boş harita
                    val consumedCaloriesMap = mutableMapOf<String, MutableList<ProductModel>>()

                    // Snapshot'lar üzerinde döngü
                    for (document in snapshots.documents) {
                        // Firestore belgesinden verileri alma işlemi
                        val partOfData = document.getString("partOfData") ?: continue
                        val product = document.getString("product") ?: continue
                        val amount = document.getString("amount") ?: continue
                        val calories = document.getString("calories") ?: continue
                        val fat = document.getString("fat") ?: continue
                        val protein = document.getString("protein") ?: continue
                        val carbohydrate = document.getString("carbohydrate") ?: continue

                        // Firestore'dan alınan verileri ProductModel nesnesine dönüştürme
                        val productModel = ProductModel(product, amount, calories, fat, protein, carbohydrate)

                        // Haritada bu tüketilen kalori türü için bir liste oluşturma
                        if (!consumedCaloriesMap.containsKey(partOfData)) {
                            consumedCaloriesMap[partOfData] = mutableListOf()
                        }

                        // Ürünü tüketilen kalori listesine ekleme
                        consumedCaloriesMap[partOfData]?.add(productModel)
                    }

                    // Oluşturulan haritadaki verileri ConsumedCalories nesnelerine dönüştürme
                    val consumedCaloriesList = consumedCaloriesMap.map { (partOfData, productModels) ->
                        ConsumedCalories(partOfData, productModels)
                    }

                    // Günlük tüketilen kalori verilerini LiveData'ya güncelleme
                    consumedCaloriesLiveData.value = consumedCaloriesList
                } else {
                    consumedCaloriesLiveData.value = null
                }
            }
    }

    // Tüketilen kalorileri Firestore'a kaydedecek fonksiyon
    fun saveConsumedCalories(consumedCalories: ConsumedCalories, callback: (Boolean) -> Unit) {
        for (product in consumedCalories.products) {
            product?.let {
                // Firestore'a kaydedilecek yeni veri hash haritası
                val newHash = hashMapOf(
                    "partOfData" to consumedCalories.partOfData,
                    "product" to it.product,
                    "amount" to it.amount,
                    "calories" to it.calories,
                    "fat" to it.fat,
                    "protein" to it.protein,
                    "carbohydrate" to it.carbohydrate
                )
                // Firestore'daki ilgili koleksiyona yeni veri ekleme işlemi
                firestore.collection(collectionPath).document(auth.currentUser!!.email.toString())
                    .collection(currentDate).add(newHash).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("daily-log", "Tüketilen kalori kaydedildi!")
                            callback(true)
                        } else {
                            Log.d("daily-log", "Hata oluştu!")
                            callback(false)
                        }
                    }.addOnFailureListener {
                        Log.d("daily-log", "Hata oluştu! ${it.message}")
                        callback(false)
                    }
            }
        }
    }

    // Kullanıcı profil bilgilerini güncelleyecek fonksiyon
    fun updateUserProfile(updateProfile: UpdateProfile, callback: (Boolean) -> Unit) {
        // Güncellenecek kullanıcı verileri hash haritası
        val userMap = hashMapOf(
            "username" to updateProfile.username,
            "age" to updateProfile.age,
            "height" to updateProfile.height,
            "weight" to updateProfile.weight,
            "calorieGoal" to updateProfile.calorieGoal,
            "goal" to updateProfile.goal,
            "activityLevel" to updateProfile.activityLevel
        )

        // Firestore'daki ilgili koleksiyondaki kullanıcı verilerini güncelleme işlemi
        firestore.collection("users").document(auth.currentUser!!.email.toString()).update(userMap as Map<String, Any>)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("daily-log", "Kullanıcı profil güncellendi!")
                    callback(true)
                } else {
                    Log.d("daily-log", "Kullanıcı profil güncellenemedi!")
                    callback(false)
                }
            }.addOnFailureListener {
                Log.d("daily-log", "Kullanıcı profil güncellenemedi! ${it.message}")
                callback(false)
            }
    }
}
