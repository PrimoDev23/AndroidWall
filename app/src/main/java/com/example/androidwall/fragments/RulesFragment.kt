package com.example.androidwall.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidwall.adapter.AppListAdapter
import com.example.androidwall.databinding.FragmentRulesBinding
import com.example.androidwall.viewmodels.RulesFragmentViewModel

class RulesFragment : Fragment() {

    private lateinit var binding : FragmentRulesBinding
    private lateinit var viewModel : RulesFragmentViewModel
    private lateinit var listAdapter: AppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRulesBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(RulesFragmentViewModel::class.java)
        viewModel.context = context!!

        //Query all installed packages
        viewModel.QueryAllPackages();

        listAdapter = AppListAdapter(viewModel.Packages.value!!, context!!)

        binding.appList.apply {
            //Set adapter and layout manager
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context!!)

            //Add divider
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        binding.fabTest.setOnClickListener {
            //Reload all packages
            viewModel.saveSettings(listAdapter.ruleSets)
        }

        initObservers()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initObservers(){
        viewModel.Packages.observe(this, Observer {
            listAdapter.ruleSets = it
            listAdapter.notifyDataSetChanged()
        })
    }
}