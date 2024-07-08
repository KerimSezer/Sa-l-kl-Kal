package com.app.sagliklikal.model

import java.io.Serializable

data class BodyPartExercisesItem(
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String,
    val id: String,
    val instructions: List<String>,
    val name: String,
    val secondaryMuscles: List<String>,
    val target: String
): Serializable