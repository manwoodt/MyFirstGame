package com.example.MyFirstGame

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.MyFirstGame.databinding.FragmentSettingsBinding

//enum class SoundThemes {BIRDS, LAUGH, SCREAM, GAME}


class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var isSoundOn = true
    private var delay:Int = 1000
    private var isHighlightOn = true
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var  binding : FragmentSettingsBinding
    private var minDelay = 100
    private var soundTheme = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBack.setOnClickListener { findNavController().popBackStack()}

        sharedPref = requireContext().getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPref.edit()

        isSoundOn = sharedPref.getBoolean("isSoundOn", true)
        binding.switchSound.isChecked = isSoundOn
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            isSoundOn = isChecked
            saveSettings()
        }


        delay = sharedPref.getInt("delay", 1000)
        binding.seekBarDelay.progress = delay
        binding.textViewDelay.text = "Задержка: ${delay.toString()} мс."
        binding.seekBarDelay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                delay = progress + minDelay
                binding.textViewDelay.text = "Задержка: ${delay.toString()} мс."
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                saveSettings()
            }
        })

        isHighlightOn = sharedPref.getBoolean("isHighlightOn", true)
        binding.switchButtonHighlight.isChecked = isHighlightOn
        binding.switchButtonHighlight.setOnCheckedChangeListener { _, isChecked ->
            isHighlightOn = isChecked
            saveSettings()
        }


        soundTheme = sharedPref.getInt("soundTheme",0)
        binding.spinnerSoundTheme.setSelection(soundTheme)
        binding.spinnerSoundTheme.onItemSelectedListener
    }
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        editor.putInt("soundTheme", position).apply()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


    private fun saveSettings() {
        editor.putBoolean("isSoundOn", isSoundOn)
        editor.putInt("delay",delay)
        editor.putBoolean("isHighlightOn", isHighlightOn)
        editor.putInt("soundTheme", soundTheme)
        editor.apply()
    }
}