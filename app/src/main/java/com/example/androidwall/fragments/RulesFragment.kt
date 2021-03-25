package com.example.androidwall.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidwall.R
import com.example.androidwall.adapter.AppListAdapter
import com.example.androidwall.databinding.FragmentRulesBinding
import com.example.androidwall.viewmodels.RulesFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RulesFragment : Fragment() {

    private lateinit var binding: FragmentRulesBinding
    private lateinit var viewModel: RulesFragmentViewModel
    private lateinit var listAdapter: AppListAdapter

    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRulesBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(RulesFragmentViewModel::class.java)
        viewModel.context = requireContext()

        if (viewModel.Rules.value!!.isEmpty()) {
            lifecycleScope.launchWhenStarted {
                withContext(Dispatchers.IO) {
                    viewModel.initValues()
                }
            }
        }

        progressDialog = Dialog(requireContext())

        progressDialog.setContentView(LayoutInflater.from(requireContext()).inflate(R.layout.progress_layout, null))
        progressDialog.setCancelable(false)

        listAdapter = AppListAdapter(viewModel.Rules.value!!, requireContext())

        binding.appList.apply {
            //Set adapter and layout manager
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext())

            //Add divider
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        binding.fabTest.setOnClickListener {
            lifecycleScope.launch {
                progressDialog.show()

                //Launch this asynchronously
                withContext(Dispatchers.IO) {
                    //Reload all packages
                    viewModel.updateRules(listAdapter.rules)
                }

                progressDialog.dismiss()
            }
        }

        initObservers()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initObservers() {
        viewModel.Rules.observe(viewLifecycleOwner, Observer {
            listAdapter.rules = it
            listAdapter.notifyDataSetChanged()
        })
    }
}