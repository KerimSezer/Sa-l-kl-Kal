package com.app.sagliklikal.model

import java.io.Serializable

data class UpdateProfile(
    val username: String,
    val age: String,
    val height: String,
    val weight: String,
    val calorieGoal: String,
    val goal: String,
    val activityLevel: String
): Serializable
