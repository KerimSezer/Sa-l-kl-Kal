package com.app.sagliklikal.model.exercise


import com.google.gson.annotations.SerializedName

data class WeightMaintaining(
    @SerializedName("back")
    val back: Back,
    @SerializedName("biceps")
    val biceps: Biceps,
    @SerializedName("chest")
    val chest: Chest,
    @SerializedName("leg")
    val leg: Leg,
    @SerializedName("shoulder")
    val shoulder: Shoulder
)