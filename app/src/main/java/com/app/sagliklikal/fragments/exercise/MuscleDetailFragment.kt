package com.app.sagliklikal.fragments.exercise

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.sagliklikal.R
import com.app.sagliklikal.adapter.ExercisesAdapter
import com.app.sagliklikal.databinding.FragmentMuscleDetailBinding
import com.app.sagliklikal.model.BodyPartExercises
import com.app.sagliklikal.model.BodyPartExercisesItem
import com.app.sagliklikal.model.MuscleGroupModel
import com.app.sagliklikal.util.TrainingType
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

/**
 * Belirli bir kas grubunun detaylarını ve egzersizlerini göstermek için kullanılan Fragment sınıfı.
 */
class MuscleDetailFragment : Fragment() {

    private lateinit var binding: FragmentMuscleDetailBinding
    private lateinit var muscleGroupModel: MuscleGroupModel
    private lateinit var userGoal: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Data binding ile FragmentMuscleDetailBinding oluşturuluyor
        binding = FragmentMuscleDetailBinding.inflate(layoutInflater)

        // Fragment'a geçirilen argümanları al
        arguments?.let {
            muscleGroupModel = it.getSerializable("muscle") as MuscleGroupModel
            userGoal = muscleGroupModel.goal
        }

        // assets içindeki exercises.json dosyasından JSON verisi okunuyor
        val jsonObject = readJsonFromAssets("exercises.json", requireContext())
        // Kullanıcı hedefine göre ilgili JSON objesinin adını belirle
        val jsonObjectName = when (userGoal) {
            TrainingType.WEIGHTGAIN.description -> "weightGain"
            TrainingType.WEIGHTLOSS.description -> "weightLoss"
            TrainingType.WEIGHTMAINTAINING.description -> "weightMaintaining"
            else -> null
        }

        // JSON objesi ve adı null değilse işleme devam et
        if (jsonObject != null && jsonObjectName != null) {
            val weightObject = jsonObject.getJSONObject(jsonObjectName)
            val exerciseObject = weightObject.getJSONObject(muscleGroupModel.key)
            val imageURL = exerciseObject.getString("imageURL")
            val bodyPart = exerciseObject.getString("bodyPart")

            // UI bileşenlerine ilgili verileri ayarla
            binding.muscleNameText.text = muscleGroupModel.muscleName
            binding.muscleNameText.text = bodyPart
            Glide.with(this).load(imageURL).into(binding.muscleImage)

            // Egzersizlerin bulunduğu JSON dizisini al
            val exercises = exerciseObject.getJSONArray("exercises")
            val bodyPartExercises = BodyPartExercises()

            // Her bir egzersiz için BodyPartExercisesItem oluştur ve listeye ekle
            for (i in 0 until exercises.length()) {
                val exercise = exercises.getJSONObject(i)
                val bodyPartExercisesItem = BodyPartExercisesItem(
                    bodyPart = exercise.getString("bodyPart"),
                    equipment = exercise.getString("equipment"),
                    gifUrl = exercise.getString("gifUrl"),
                    id = exercise.getString("id"),
                    instructions = exercise.getJSONArray("instructions").toList(),
                    name = exercise.getString("name"),
                    secondaryMuscles = exercise.getJSONArray("secondaryMuscles").toList(),
                    target = exercise.getString("target")
                )
                bodyPartExercises.add(bodyPartExercisesItem)
            }

            // RecyclerView için ExercisesAdapter oluştur
            val adapter = ExercisesAdapter(bodyPartExercises)
            binding.exerciseRecycler.layoutManager = LinearLayoutManager(requireContext())
            binding.exerciseRecycler.adapter = adapter

        } else {
            // JSON objesi veya adı null ise hata kaydı oluştur
            Log.e("MuscleDetailFragment", "JSON object or name is null")
        }

        return binding.root
    }

    /**
     * assets klasöründeki JSON dosyasını okuyan yardımcı fonksiyon.
     */
    private fun readJsonFromAssets(fileName: String, context: Context): JSONObject? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            JSONObject(String(buffer, Charsets.UTF_8))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

/**
 * JSONArray'i String listesine dönüştüren yardımcı fonksiyon.
 */
private fun JSONArray.toList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until this.length()) {
        list.add(this.getString(i))
    }
    return list
}
