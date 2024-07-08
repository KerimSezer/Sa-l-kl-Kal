package com.app.sagliklikal.util

// Öğün tiplerini temsil eden enum sınıfı
enum class MealType(val description: String) {
    // Enum sabitleri ve açıklamaları
    BREAKFAST("Kahvaltı"), // Kahvaltı öğünü
    LAUNCH("Öğle Yemeği"), // Öğle yemeği öğünü
    DINNER("Akşam Yemeği"), // Akşam yemeği öğünü
    OTHER("Aperatifler/Diğer") // Aperatifler veya diğer öğünleri temsil eder
}
