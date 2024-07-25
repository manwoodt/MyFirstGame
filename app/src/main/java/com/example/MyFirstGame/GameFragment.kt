package com.example.MyFirstGame

import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.MyFirstGame.databinding.FragmentGameBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : BaseGameFragment() {
    private lateinit var binding: FragmentGameBinding

    private val userSequence = mutableListOf<Int>()
    private val rightSequence = mutableListOf<Int>()
    private var highScore = 0
    private var level = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        } else {
            loadSettings()
            loadHighScore()
            init()
            startGame()
        }
    }


    private fun init() {
        initializeButtons()
        initializeColors()
        initializeSounds()
        clickButtons()
        binding.buttonBack.setOnClickListener {
            if (level > highScore) highScore = level
            binding.textViewHighscore.text = "Рекорд: $highScore"
            saveHighScore()
            findNavController().popBackStack() }
    }

    private fun initializeButtons() {
      buttons  = arrayOf(
            binding.button1, binding.button2, binding.button3, binding.button4
        )
    }

    private fun startGame() {
        rightSequence.clear()
        playLevel()
    }

    private fun playLevel() {
        userSequence.clear()
        level++
        binding.textViewLevel.text = "Уровень: $level"
        addSoundToSequence()
        lifecycleScope.launch { playSequence() }
    }

    private fun addSoundToSequence() {
        val newSound = (0..3).random()
        rightSequence.add(newSound)
    }

    private suspend fun playSequence() {
        buttons.forEach { it.isEnabled = false }
        delay(500)
        for (i in rightSequence) {
            changeBackground(i)
            playMusic(i)
            delay(delay.toLong())
        }
        buttons.forEach { it.isEnabled = true }
    }


    override fun onButtonClicked(index: Int) {
        userSequence.add(index)
        changeBackground(index)
        playMusic(index)
        if (index != rightSequence[userSequence.size - 1]) {
            gameOver()
        } else if (userSequence.size == rightSequence.size) {
            checkSequence()
        }
    }

    private fun checkSequence() {
        if (userSequence == rightSequence) playLevel()
        else gameOver()

    }


    private fun gameOver() {
        if (level > highScore) highScore = level
        binding.textViewHighscore.text = "Рекорд: $highScore"
        saveHighScore()
        AlertDialog.Builder(requireContext())
            .setTitle("Игра окончена")
            .setMessage("Ваш уровень: $level\nРекорд: $highScore")
            .setPositiveButton("Рестарт") { _, _ -> startGame() }
            .setNegativeButton("Меню") { _, _ -> findNavController().popBackStack() }
            .setCancelable(false)
            .show()
        level = 0
    }

    private fun saveHighScore() {
        val sharedPref =
            requireActivity().getSharedPreferences("HighScore", AppCompatActivity.MODE_PRIVATE)
                .edit()
        sharedPref.putInt("HighScore", highScore)
        sharedPref.apply()
    }

    private fun loadHighScore() {
        val sharedPref =
            requireActivity().getSharedPreferences("HighScore", AppCompatActivity.MODE_PRIVATE)
        highScore = sharedPref.getInt("HighScore", 0)
        binding.textViewHighscore.text = "Рекорд: $highScore"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("userSequence", ArrayList(userSequence))
        outState.putIntegerArrayList("rightSequence", ArrayList(rightSequence))
        outState.putInt("highScore", highScore)
        outState.putInt("level", level)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        userSequence.clear()
        rightSequence.clear()
        userSequence.addAll(savedInstanceState.getIntegerArrayList("userSequence") ?: emptyList())
        rightSequence.addAll(savedInstanceState.getIntegerArrayList("rightSequence") ?: emptyList())
        highScore = savedInstanceState.getInt("highScore", 0)
        level = savedInstanceState.getInt("level", 0)

        binding.textViewLevel.text = "Уровень: $level"
        binding.textViewHighscore.text = "Рекорд: $highScore"

        lifecycleScope.launch { playSequence() }
    }

}