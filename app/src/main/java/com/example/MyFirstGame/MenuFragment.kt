package com.example.MyFirstGame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.MyFirstGame.databinding.FragmentMenuBinding


class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.buttonNewGame.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_gameFragment) }
        binding.buttonFreePlay.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_freePlay) }
        binding.buttonAbout.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_aboutFragment) }
        binding.buttonSettings.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_settingsFragment) }

        return binding.root
    }

    companion object {
        fun newInstance() =
            MenuFragment()
    }
}