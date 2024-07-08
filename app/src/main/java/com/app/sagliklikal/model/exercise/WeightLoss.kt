package com.app.sagliklikal.model.exercise


import com.google.gson.annotations.SerializedName

data class WeightLoss(
    @SerializedName("cardio")
    val cardio: Cardio
)