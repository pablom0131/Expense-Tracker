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
    private val _expenses: MutableStateFlow<List<Expense>> = MutableStateFlow(emptyList())
    val expenses: StateFlow<List<Expense>> get() = _expenses.asStateFlow()

    private val _totalExpensesByCategory: MutableStateFlow<Map<String, Expense>> =
        MutableStateFlow(emptyMap())
    val totalExpensesByCategory: StateFlow<Map<String, Expense>> get() = _totalExpensesByCategory.asStateFlow()


    init {
        viewModelScope.launch {
            expenseRepository.getExpenses().collect { expensesList ->
                _expenses.value = expensesList

                val totalExpensesMap = expensesList
                    .groupingBy { it.category }
                    //the below statement is likely broken
                    .reduce { _, accumulator, expense -> accumulator.apply { expense.amount } }

                _totalExpensesByCategory.value = totalExpensesMap
            }
        }
    }
}
