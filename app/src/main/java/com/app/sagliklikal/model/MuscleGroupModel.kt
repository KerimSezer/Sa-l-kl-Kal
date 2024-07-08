package com.app.sagliklikal.model

import java.io.Serializable

data class MuscleGroupModel(
    val key: String,
    val goal: String,
    val muscleName: String,
    val muscleImageURL: String
) : Serializable