package com.example.MyFirstGame


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.MyFirstGame.databinding.FragmentFreePlayBinding

class FreePlayFragment : BaseGameFragment() {
    private lateinit var binding: FragmentFreePlayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFreePlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()
        init()
    }

    private fun initializeButtons(){
        buttons = arrayOf(
            binding.button1, binding.button2, binding.button3, binding.button4
        )
    }

    private fun init() {
        initializeButtons()
        initializeColors()
        initializeSounds()
        clickButtons()
        binding.buttonBack.setOnClickListener { findNavController().popBackStack() }

    }


}