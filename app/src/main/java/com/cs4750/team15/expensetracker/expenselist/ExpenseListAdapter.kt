package com.cs4750.team15.expensetracker.expenselist

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cs4750.team15.expensetracker.databinding.ListItemExpenseBinding
import java.util.UUID

private const val DATE_FORMAT = "MMMM dd, yyyy"

class ExpenseHolder(private val binding: ListItemExpenseBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(expense: Expense, onExpenseClicked: (expenseId: UUID) -> Unit) {
        binding.expenseTitle.text = expense.title
        binding.expenseDate.text = DateFormat.format(DATE_FORMAT, expense.date)
        binding.expenseAmount.text = "$" + expense.amount.toString()
        binding.expenseCategory.text = "⚭ " + expense.category

        binding.root.setOnClickListener {
            onExpenseClicked(expense.id)
        }
    }
}

class ExpenseListAdapter(
    private val expenses: List<Expense>,
    private val onExpenseClicked: (expenseID: UUID) -> Unit
):  RecyclerView.Adapter<ExpenseHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemExpenseBinding.inflate(inflater, parent, false)
        return ExpenseHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseHolder, position: Int) {
        val expense = expenses[position]
        holder.bind(expense, onExpenseClicked)
    }

    override fun getItemCount() = expenses.size
}