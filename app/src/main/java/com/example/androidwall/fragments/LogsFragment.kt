package com.example.androidwall.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidwall.viewmodels.LogsFragmentViewModel
import com.example.androidwall.R
import com.example.androidwall.adapter.LogsAdapter
import com.example.androidwall.databinding.LogsFragmentBinding

class LogsFragment : Fragment() {

    companion object {
        fun newInstance() = LogsFragment()
    }

    private lateinit var binding : LogsFragmentBinding
    private lateinit var viewModel: LogsFragmentViewModel
    private lateinit var logsAdapter : LogsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LogsFragmentBinding.inflate(inflater, container, false)

        //Create the viewmodel
        viewModel = ViewModelProvider(this).get(LogsFragmentViewModel::class.java)

        viewModel.readLogs()

        //Init recyclerview
        logsAdapter = LogsAdapter(viewModel.Logs.value!!)

        binding.logList.apply {
            adapter = logsAdapter
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        binding.logList.scrollToPosition(viewModel.Logs.value!!.size - 1)

        return binding.root
    }

}