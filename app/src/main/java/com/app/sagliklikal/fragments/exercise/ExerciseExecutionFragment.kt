package com.app.sagliklikal.fragments.exercise

import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.app.sagliklikal.R
import com.app.sagliklikal.databinding.FragmentExerciseExecutionBinding
import com.app.sagliklikal.model.BodyPartExercisesItem
import com.app.sagliklikal.model.ExecutionModel
import com.app.sagliklikal.util.CountDownDialog
import com.app.sagliklikal.util.CustomProgressDialog
import com.app.sagliklikal.util.downloadGifFromURL
import com.app.sagliklikal.util.downloadImageFromURL
import com.app.sagliklikal.viewmodel.ExecutionViewModel

// Egzersiz yapma işlemlerini yöneten Fragment sınıfı
class ExerciseExecutionFragment : Fragment() {

    // Değişkenlerin tanımlanması
    private var isRunning = false
    private var elapsedSet = 0
    private var elapsedTime: Long = 0

    private var enteredSets = "0"
    private var enteredReps = "0"
    private var enteredWeight = "0"
    private var enteredType = ""

    private lateinit var binding: FragmentExerciseExecutionBinding
    private lateinit var customProgress: CustomProgressDialog
    private lateinit var countDownDialog: CountDownDialog
    private lateinit var exerciseItem: BodyPartExercisesItem
    private val executionViewModel: ExecutionViewModel by viewModels()

    // Fragment oluşturulduğunda çağrılan metod
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExerciseExecutionBinding.inflate(inflater)
        customProgress = CustomProgressDialog(requireContext())
        countDownDialog = CountDownDialog(requireContext())

        // Argümanlardan egzersiz öğesini al ve UI'ı güncelle
        arguments?.let {
            exerciseItem = it.getSerializable("exercise") as BodyPartExercisesItem

            with(binding) {
                exerciseGifView.downloadImageFromURL(exerciseItem.gifUrl)
                exerciseName.text = exerciseItem.name
            }
        }

        // Click listener'ları ayarla
        setClickListeners()
        // Bottom sheet'i göster
        showBottomSheet()

        return binding.root
    }

    // Click listener'ları ayarlayan metod
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClickListeners() {
        with(binding) {
            startButton.setOnClickListener {
                handleStartButtonClick()
            }

            nextSetButton.setOnClickListener {
                handleNextSetButtonClick()
            }

            pauseAndPlay.setOnClickListener {
                handlePausePlayClick()
            }

            settings.setOnClickListener {
                showBottomSheet()
            }
        }
    }

    // Başlangıç düğmesine tıklama işlemini yöneten metod
    private fun handleStartButtonClick() {
        if (enteredSets.toInt() != 0 && enteredReps.toInt() != 0) {
            countDownDialog.showCountDownDialog(5000) { started ->
                if (started) {
                    handleExerciseStart()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Lütfen tekrar sayısını ve setlerini girin.", Toast.LENGTH_SHORT).show()
        }
    }

    // Egzersize başlanmasını sağlayan metod
    private fun handleExerciseStart() {
        with(binding) {
            exerciseGifView.downloadGifFromURL(exerciseItem.gifUrl)
            startButton.visibility = View.GONE
            startedLayout.visibility = View.VISIBLE
            startChronometer()
        }
    }

    // Sonraki set düğmesine tıklama işlemini yöneten metod
    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleNextSetButtonClick() {
        if (elapsedSet != 1) {
            handleNextSet()
        } else {
            saveExerciseData()
        }
    }

    // Sonraki set işlemini yöneten metod
    private fun handleNextSet() {
        with(binding) {
            exerciseGifView.downloadImageFromURL(exerciseItem.gifUrl)
            chronometer.stop()
            countDownDialog.showCountDownDialog(1000) {
                resetChronometer()
                updateNextSetUI()
            }
        }
    }

    // Kronometreyi sıfırlayan metod
    private fun resetChronometer() {
        with(binding) {
            pauseAndPlay.setBackgroundResource(R.drawable.round_6dp_background)
            pauseAndPlay.setImageResource(R.drawable.baseline_play_arrow_24)
            chronometer.base = SystemClock.elapsedRealtime()
            elapsedTime = 0
            isRunning = false
            if (elapsedSet > 1) {
                elapsedSet--
                elapsedSetText.text = "$elapsedSet x Yapılan Setler"
            }
        }
    }

    // Sonraki set UI'ını güncelleyen metod
    private fun updateNextSetUI() {
        with(binding) {
            if (elapsedSet == 1) nextSetButton.text = getString(R.string.finish_workout)
        }
    }

    // Egzersiz verilerini kaydeden metod
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveExerciseData() {
        // Güncel yılı al
        val currentYear: String = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        // Egzersiz modelini oluştur
        val exerciseData = ExecutionModel(exerciseItem.name, enteredWeight.toDouble(), enteredType, enteredSets.toInt(), enteredReps.toInt(), currentYear)
        customProgress.show() // İlerleme çubuğunu göster
        // ViewModel üzerinden egzersiz verilerini kaydet
        executionViewModel.saveWorkout(exerciseData) { result, message ->
            customProgress.dismiss() // İlerleme çubuğunu gizle
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show() // Kullanıcıya mesaj göster
            if (result) {
                // Başarılı ise egzersiz ekranına geri dön
                val action = ExerciseExecutionFragmentDirections.actionExerciseExecutionFragmentToFragmentExercise()
                findNavController().navigate(action)
            }
        }
    }

    // Duraklat/Oynat düğmesine tıklama işlemini yöneten metod
    private fun handlePausePlayClick() {
        if (isRunning) {
            pauseChronometer()
        } else {
            playChronometer()
        }
    }

    // Kronometreyi duraklatan metod
    private fun pauseChronometer() {
        with(binding) {
            val chronometer = this.chronometer
            elapsedTime = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.stop()
            isRunning = false
            exerciseGifView.downloadImageFromURL(exerciseItem.gifUrl)
            pauseAndPlay.setBackgroundResource(R.drawable.round_6dp_background)
            pauseAndPlay.setImageResource(R.drawable.baseline_play_arrow_24)
        }
    }

    // Kronometreyi başlatan metod
    private fun playChronometer() {
        with(binding) {
            val chronometer = this.chronometer
            chronometer.base = SystemClock.elapsedRealtime() - elapsedTime
            chronometer.start()
            isRunning = true
            exerciseGifView.downloadGifFromURL(exerciseItem.gifUrl)
            pauseAndPlay.setBackgroundResource(R.drawable.round_6dp_background)
            pauseAndPlay.setImageResource(R.drawable.baseline_pause_24)
            if (elapsedSet == 1) nextSetButton.text = getString(R.string.finish_workout)
        }
    }

    // Bottom sheet'i gösteren metod
    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_execution, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        var selectedType: String? = null

        val weightTypes = listOf("kg", "lbs")
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_items, weightTypes)
        val typeDropDown = view.findViewById<AutoCompleteTextView>(R.id.typeDropDown)
        typeDropDown.setAdapter(typeAdapter)

        val weightEditText = view.findViewById<TextInputEditText>(R.id.weightText)
        val setsEditText = view.findViewById<TextInputEditText>(R.id.setsText)
        val repsEditText = view.findViewById<TextInputEditText>(R.id.repsText)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            // Girilen verileri al
            enteredWeight = weightEditText.text.toString()
            enteredSets = setsEditText.text.toString()
            enteredReps = repsEditText.text.toString()

            // Boş alan kontrolü yap
            if (listOf(enteredWeight, enteredSets, enteredReps, selectedType).any { it.isNullOrEmpty() }) {
                Toast.makeText(requireContext(), getString(R.string.please_fill_in_the_empty_fields), Toast.LENGTH_SHORT).show()
            } else {
                // Seçilen verileri UI'a yerleştir
                binding.selectedWeight.text = "$enteredWeight $selectedType"
                binding.selectedSets.text = "$enteredSets Set"
                binding.selectedReps.text = "$enteredReps Tekrar"
                elapsedSet = enteredSets.toInt()
                binding.elapsedSetText.text = "$elapsedSet x Yapılan Setler"
                bottomSheetDialog.dismiss()
            }
        }

        // Tip dropdown'ında seçim yapıldığında
        typeDropDown.setOnItemClickListener { adapterView, view, i, l ->
            selectedType = adapterView.getItemAtPosition(i).toString()
            enteredType = selectedType.toString()
        }

        bottomSheetDialog.show()
    }

    // Kronometreyi başlatan veya durduran metod
    private fun startChronometer() {
        val chronometer = binding.chronometer
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
            isRunning = true
        } else {
            chronometer.stop()
            isRunning = false
        }
    }
}
