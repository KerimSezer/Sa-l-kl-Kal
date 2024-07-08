package com.app.sagliklikal.model.exercise


import com.google.gson.annotations.SerializedName

data class TrainingModel(
    @SerializedName("weightGain")
    val weightGain: WeightGain,
    @SerializedName("weightLoss")
    val weightLoss: WeightLoss,
    @SerializedName("weightMaintaining")
    val weightMaintaining: WeightMaintaining
)