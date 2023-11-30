package com.cs4750.team15.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cs4750.team15.expensetracker.databinding.FragmentWelcomeScreenBinding

class WelcomeFragment: Fragment() {

    private var _binding: FragmentWelcomeScreenBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "Cannot access binding"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeScreenBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            expenseListButton.setOnClickListener {
                findNavController().navigate(WelcomeFragmentDirections.showExpenseList())
            }

            spendingAnalysisButton.setOnClickListener {
                findNavController().navigate(WelcomeFragmentDirections.showSpendingAnalysis())
            }

            chatButton.setOnClickListener {  }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}