package com.example.androidwall.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.androidwall.R
import com.example.androidwall.databinding.OptionsFragmentBinding
import com.example.androidwall.models.FirewallMode
import com.example.androidwall.viewmodels.RulesFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OptionsFragment : Fragment() {

    private lateinit var binding: OptionsFragmentBinding
    private lateinit var rulesViewModel: RulesFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = OptionsFragmentBinding.inflate(inflater, container, false)

        rulesViewModel =
            ViewModelProvider(requireActivity()).get(RulesFragmentViewModel::class.java)

        binding.layoutMode.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    //Toggle mode on click
                    rulesViewModel.toggleMode()
                }

                //Set the text
                setModeText()
            }
        }

        binding.layoutEnabled.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    //Toggle enabled status
                    rulesViewModel.toggleEnabled()
                }
                //Set the new text according to new status
                setStatusText()
            }
        }

        setModeText()
        setStatusText()

        return binding.root
    }

    private fun setModeText() {
        binding.txtFirewallmode.text =
            if (rulesViewModel.Settings.value!!.mode == FirewallMode.WHITELIST) getString(R.string.Whitelist) else getString(
                R.string.Blacklist
            )
    }

    private fun setStatusText() {
        binding.txtStatus.text =
            if (rulesViewModel.Settings.value!!.enabled) getString(R.string.Enabled) else getString(
                R.string.Disabled
            )
    }
}