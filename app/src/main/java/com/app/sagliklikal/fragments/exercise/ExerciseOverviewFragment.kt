package com.app.sagliklikal.fragments.exercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.FragmentExerciseOverviewBinding
import com.app.sagliklikal.model.BodyPartExercisesItem
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Egzersizin genel bir özetini göstermek için kullanılan Fragment sınıfı.
 */
class ExerciseOverviewFragment : Fragment() {

    private lateinit var exerciseItem: BodyPartExercisesItem
    private lateinit var exerciseOverviewBinding: FragmentExerciseOverviewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Data binding ile FragmentExerciseOverviewBinding oluşturuluyor
        exerciseOverviewBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_exercise_overview, container, false)
        // BottomNavigationView'u gizle
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE

        // Fragment'a geçirilen argümanları al
        arguments?.let {
            exerciseItem = it.getSerializable("exercise") as BodyPartExercisesItem
            // Data binding ile egzersiz verisini bağla
            exerciseOverviewBinding.exercise = exerciseItem
            // Egzersiz talimatlarını TextView'a ayarla
            setInstructionsToTextView(exerciseItem.instructions)
        }

        return exerciseOverviewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 'Haydi Yapalım' butonuna tıklanınca egzersiz uygulama ekranına geçiş yap
        exerciseOverviewBinding.letsDoItButton.setOnClickListener {
            val action = ExerciseOverviewFragmentDirections.actionExerciseOverviewFragmentToExerciseExecutionFragment(exerciseItem)
            findNavController().navigate(action)
        }
    }

    /**
     * Egzersiz talimatlarını formatlayıp TextView'a ayarlayan yardımcı fonksiyon.
     */
    private fun setInstructionsToTextView(instructions: List<String>) {
        val stringBuilder = StringBuilder()
        var instructionNumber = 1

        // Her bir talimat için sırayla numaralandırılmış şekilde metin oluştur
        for (instruction in instructions) {
            stringBuilder.append("$instructionNumber. $instruction\n")
            instructionNumber++
        }

        // TextView'a oluşturulan metni ayarla
        exerciseOverviewBinding.instructionsText.text = stringBuilder.toString()
    }
}
