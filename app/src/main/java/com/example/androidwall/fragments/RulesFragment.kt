package com.example.androidwall.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidwall.R
import com.example.androidwall.adapter.AppListAdapter
import com.example.androidwall.databinding.FragmentRulesBinding
import com.example.androidwall.models.FirewallMode
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
        viewModel.context = requireContext()

        //Query all installed packages
        viewModel.QueryAllPackages();

        listAdapter = AppListAdapter(viewModel.Packages.value!!, requireContext())

        binding.appList.apply {
            //Set adapter and layout manager
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext())

            //Add divider
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        binding.fabTest.setOnClickListener {
            //Reload all packages
            viewModel.saveSettings(listAdapter.ruleSets)
        }

        initObservers()

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initObservers(){
        viewModel.Packages.observe(viewLifecycleOwner, Observer {
            listAdapter.ruleSets = it
            listAdapter.notifyDataSetChanged()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbarmenu, menu)
        changeIcon(menu.getItem(0))
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.toggle_mode -> toggleMode(item)
        }
        return true
    }

    private fun toggleMode(item : MenuItem){
        viewModel.toggleMode()
        listAdapter.ruleSets = viewModel.Packages.value!!
        listAdapter.notifyDataSetChanged()

        changeIcon(item)
    }

    private fun changeIcon(item : MenuItem){
        when(viewModel.Packages.value!!.mode){
            FirewallMode.WHITELIST -> item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_whitelist)
            FirewallMode.BLACKLIST -> item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_blacklist)
        }
    }
}