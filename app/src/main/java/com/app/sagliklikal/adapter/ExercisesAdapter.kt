package com.app.sagliklikal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.ItemMuscleDetailsBinding
import com.app.sagliklikal.fragments.exercise.MuscleDetailFragmentDirections
import com.app.sagliklikal.model.BodyPartExercises

// Vücut bölgesi egzersizlerini gösteren RecyclerView için adaptör sınıfı
class ExercisesAdapter(private val bodyPartExercises: BodyPartExercises) : RecyclerView.Adapter<ExercisesAdapter.ItemHolder>() {

    // View tutucu sınıfı
    inner class ItemHolder(val itemMuscleDetailsBinding: ItemMuscleDetailsBinding) : RecyclerView.ViewHolder(itemMuscleDetailsBinding.root) {}

    // Yeni bir ViewHolder oluşturulduğunda çağrılır
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        // Veri bağlamak için veri bağlama kütüphanesi kullanarak View oluştur
        val view = DataBindingUtil.inflate<ItemMuscleDetailsBinding>(LayoutInflater.from(parent.context), R.layout.item_muscle_details, parent, false)
        return ItemHolder(view)
    }

    // Adaptörün tuttuğu öğe sayısını döndürür
    override fun getItemCount(): Int {
        return bodyPartExercises.size
    }

    // ViewHolder'ın bağlandığı pozisyon için veri bağlama işlemi
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        // Pozisyondaki egzersiz verisini al
        val exercise = bodyPartExercises[position]
        // View üzerindeki tıklama işlemi
        holder.itemMuscleDetailsBinding.muscleDetailLinear.setOnClickListener {
            // Yeni bir navigasyon aksiyonu oluştur ve doğru fragment'a geçiş yap
            val action = MuscleDetailFragmentDirections.actionMuscleDetailFragmentToExerciseOverviewFragment(exercise)
            it.findNavController().navigate(action)
        }
        // View'e egzersiz verisini bağla
        holder.itemMuscleDetailsBinding.exercise = exercise
    }
}
