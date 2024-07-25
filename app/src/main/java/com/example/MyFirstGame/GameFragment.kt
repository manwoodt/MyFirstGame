package com.example.MyFirstGame

import android.animation.ObjectAnimator
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

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var sounds: Array<MediaPlayer>
    private val buttons by lazy { initializeButtons() }
    private val colors by lazy { initializeColors() }
    private val userSequence = mutableListOf<Int>()
    private val rightSequence = mutableListOf<Int>()
    private var level = 0
    private var highScore = 0
    private var soundTheme = 0
    private var isSoundOn = true
    private var isHighlightOn = true
    private var delay: Int = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()
        initializeSounds()
        initializeUI()
        loadHighScore()
        startGame()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GameFragment()
    }

    private fun initializeUI() {
        buttons.forEachIndexed { index, button ->
            button.setBackgroundColor(colors[index])
            button.setOnClickListener { onButtonClicked(index) }
        }
        binding.buttonBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initializeButtons() = arrayOf(
        binding.button1, binding.button2, binding.button3, binding.button4
    )

    private fun initializeSounds() {
        sounds =  when (soundTheme) {
            0 ->  arrayOf(
                createMediaPlayerFromAssets("sounds/birdsSounds/cartoon_bird.mp3"),
                createMediaPlayerFromAssets("sounds/birdsSounds/chirp.mp3"),
                createMediaPlayerFromAssets("sounds/birdsSounds/eric.mp3"),
                createMediaPlayerFromAssets("sounds/birdsSounds/hirp.mp3")
            )
            1 -> arrayOf(
                createMediaPlayerFromAssets("sounds/gameSounds/extra-bonus.wav"),
                createMediaPlayerFromAssets("sounds/gameSounds/negative-guitar-tone.wav"),
                createMediaPlayerFromAssets("sounds/gameSounds/unlock-game.wav"),
                createMediaPlayerFromAssets("sounds/gameSounds/winning-a-coin.wav")
            )
            2 ->  arrayOf(
                createMediaPlayerFromAssets("sounds/laughSounds/cartoon-giggle.wav"),
                createMediaPlayerFromAssets("sounds/laughSounds/cartoon-laugh.wav"),
                createMediaPlayerFromAssets("sounds/laughSounds/dwarf-laugh.wav"),
                createMediaPlayerFromAssets("sounds/laughSounds/female-laugh.wav")
            )
            else -> arrayOf(
                createMediaPlayerFromAssets("sounds/screamSounds/cartoon-panic-squeak.wav"),
                createMediaPlayerFromAssets("sounds/screamSounds/cockatoo-squawk.wav"),
                createMediaPlayerFromAssets("sounds/screamSounds/monster-growl.wav"),
                createMediaPlayerFromAssets("sounds/screamSounds/woman-pain.wav")
            )
        }

    }

    private fun createMediaPlayerFromAssets(filePath: String): MediaPlayer {
        val assetManager = requireContext().assets
        val mediaPlayer = MediaPlayer()

        val afd = assetManager.openFd(filePath)
        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        mediaPlayer.prepare()

        return mediaPlayer
    }

    private fun initializeColors() = arrayOf(
        ContextCompat.getColor(requireContext(), R.color.button_color_1),
        ContextCompat.getColor(requireContext(), R.color.button_color_2),
        ContextCompat.getColor(requireContext(), R.color.button_color_3),
        ContextCompat.getColor(requireContext(), R.color.button_color_4)
    )

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

    private fun changeBackground(index: Int) {
        if (isHighlightOn) {
            val button = buttons[index]
            val originalColor = colors[index]
            val newColor = ContextCompat.getColor(requireContext(), R.color.black)

            ObjectAnimator.ofArgb(button, "backgroundColor", newColor, originalColor).apply {
                duration = 1000
                start()
            }
        }
    }

    private fun playMusic(index: Int) {
        if (isSoundOn) {
            val music = sounds[index]
            music.start()
        }
    }

    private fun onButtonClicked(index: Int) {
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
        if (userSequence == rightSequence) {
            playLevel()
        } else {
            gameOver()
        }
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

    private fun loadSettings() {
        val sharedPref =
            requireActivity().getSharedPreferences("Settings", AppCompatActivity.MODE_PRIVATE)
        isSoundOn = sharedPref.getBoolean("isSoundOn", true)
        delay = sharedPref.getInt("delay", 1000)
        isHighlightOn = sharedPref.getBoolean("isHighlightOn", true)
        soundTheme = sharedPref.getInt("soundTheme", 0)
    }


    override fun onDestroy() {
        super.onDestroy()
        sounds.forEach { it.release() }
    }

}