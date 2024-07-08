package com.app.sagliklikal.util

// Antrenman tiplerini temsil eden enum sınıfı
enum class TrainingType(val description: String) {
    // Enum sabitleri ve açıklamaları
    WEIGHTGAIN("Kilo Alımı"), // Kilo alımı için antrenman tipi
    WEIGHTLOSS("Kilo Kaybı"), // Kilo kaybı için antrenman tipi
    WEIGHTMAINTAINING("Kilo Koruma") // Kilo koruma için antrenman tipi
}
