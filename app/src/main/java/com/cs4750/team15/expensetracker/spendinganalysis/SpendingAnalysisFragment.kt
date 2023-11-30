package com.cs4750.team15.expensetracker.spendinganalysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cs4750.team15.expensetracker.databinding.SpendingAnalysisBinding

class SpendingAnalysisFragment: Fragment() {
    private var _binding: SpendingAnalysisBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "Cannot access binding"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SpendingAnalysisBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}