package com.example.MyFirstGame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.MyFirstGame.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {
    private lateinit var binding : FragmentAboutBinding
    private var highScore = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

       binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBack.setOnClickListener { findNavController().popBackStack() }
        loadHighScore()
    }

    private fun loadHighScore() {
        val sharedPref =
            requireActivity().getSharedPreferences("HighScore", AppCompatActivity.MODE_PRIVATE)
        highScore = sharedPref.getInt("HighScore", 0)
        binding.textViewHighscore.text = "\nТвой рекорд: $highScore"
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AboutFragment()
    }

}