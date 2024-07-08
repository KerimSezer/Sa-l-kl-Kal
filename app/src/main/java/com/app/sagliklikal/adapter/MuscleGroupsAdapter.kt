package com.app.sagliklikal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.ItemMuscleGroupsBinding
import com.app.sagliklikal.fragments.exercise.ExerciseFragmentDirections
import com.app.sagliklikal.model.MuscleGroupModel

// Kas gruplarını gösteren RecyclerView için adaptör sınıfı
class MuscleGroupsAdapter(private val muscleGroups: ArrayList<MuscleGroupModel>) : RecyclerView.Adapter<MuscleGroupsAdapter.ItemHolder>() {

    // View tutucu sınıfı
    inner class ItemHolder(val itemMuscleGroupsBinding: ItemMuscleGroupsBinding) : RecyclerView.ViewHolder(itemMuscleGroupsBinding.root)

    // Yeni bir ViewHolder oluşturulduğunda çağrılır
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        // Veri bağlamak için veri bağlama kütüphanesi kullanarak View oluştur
        val view = DataBindingUtil.inflate<ItemMuscleGroupsBinding>(LayoutInflater.from(parent.context), R.layout.item_muscle_groups, parent, false)
        return ItemHolder(view)
    }

    // Adaptörün tuttuğu öğe sayısını döndürür
    override fun getItemCount(): Int {
        return muscleGroups.size
    }

    // ViewHolder'ın bağlandığı pozisyon için veri bağlama işlemi
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        // Pozisyondaki kas grubu verisini al
        val muscle = muscleGroups[position]
        // View üzerindeki tıklama işlemi
        holder.itemMuscleGroupsBinding.muscleRootFrame.setOnClickListener {
            // Yeni bir navigasyon aksiyonu oluştur ve doğru fragment'a geçiş yap
            val action = ExerciseFragmentDirections.actionFragmentExerciseToMuscleDetailFragment(muscle)
            it.findNavController().navigate(action)
        }
        // View'e kas grubu verisini bağla
        holder.itemMuscleGroupsBinding.muscle = muscle
    }
}
