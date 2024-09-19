package com.example.MyFirstGame

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class BaseGameFragment : Fragment() {


    private lateinit var sounds: Array<MediaPlayer>
    protected lateinit var buttons: Array<Button>
    private lateinit var colors: Array<Int>
    private var isSoundOn = true
    private var isHighlightOn = true
    protected var delay: Int = 1000
    private var soundTheme = 0

    protected fun initializeSounds() {
        sounds = when (soundTheme) {
            0 -> arrayOf(
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

            2 -> arrayOf(
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

    protected fun initializeColors() {
        colors = arrayOf(
            ContextCompat.getColor(requireContext(), R.color.button_color_1),
            ContextCompat.getColor(requireContext(), R.color.button_color_2),
            ContextCompat.getColor(requireContext(), R.color.button_color_3),
            ContextCompat.getColor(requireContext(), R.color.button_color_4)
        )
    }

    protected fun clickButtons() {
        buttons.forEachIndexed { index, button ->
            button.setBackgroundColor(colors[index])
            changeBackground(index)
            button.setOnClickListener { onButtonClicked(index) }
        }
    }

    protected open fun onButtonClicked(index: Int) {
        changeBackground(index)
        playMusic(index)
    }

    protected fun changeBackground(index: Int) {
        if (isHighlightOn) {
            val button = buttons[index]
            val originalColor = colors[index]
            val newColor = ContextCompat.getColor(requireContext(), R.color.black)

            ObjectAnimator.ofArgb(button, "backgroundColor", newColor, originalColor).apply {
                duration = 500
                start()
            }
        }
    }

    protected fun playMusic(index: Int) {
        if (isSoundOn) {
            val music = sounds[index]
            music.start()
        }
    }

    protected fun loadSettings() {
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



