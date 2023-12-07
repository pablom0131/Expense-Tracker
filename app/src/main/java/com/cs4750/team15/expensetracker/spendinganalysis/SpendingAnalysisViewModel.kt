package com.cs4750.team15.expensetracker.spendinganalysis
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs4750.team15.expensetracker.expenselist.Expense
import com.cs4750.team15.expensetracker.expenselist.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpendingAnalysisViewModel : ViewModel() {
    private val expenseRepository = ExpenseRepository.get()
    private val _expenses: List<Expense> = expenseRepository.getSimpleExpenses()

    fun getExpenses(): List<Expense> {
        return _expenses
    }
}
