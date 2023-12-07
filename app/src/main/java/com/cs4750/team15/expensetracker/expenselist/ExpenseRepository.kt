package com.cs4750.team15.expensetracker.expenselist

import android.content.Context
import androidx.room.Room
import com.cs4750.team15.expensetracker.expenselist.database.ExpenseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "expense-database"

class ExpenseRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    private val database: ExpenseDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            ExpenseDatabase::class.java,
            DATABASE_NAME
        )
        .build()

    fun getExpenses(): Flow<List<Expense>> = database.expenseDao().getExpenses()

    fun getSimpleExpenses(): List<Expense> = database.expenseDao().getSimpleExpenses()

    suspend fun getExpense(id: UUID): Expense = database.expenseDao().getExpense(id)

    fun updateExpense(expense: Expense) {
        coroutineScope.launch {
            database.expenseDao().updateExpense(expense)
        }
    }

    suspend fun addExpense(expense: Expense){
        database.expenseDao().addExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        database.expenseDao().deleteExpense(expense)
    }

    companion object {
        private var INSTANCE: ExpenseRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = ExpenseRepository(context)
            }
        }

        fun get(): ExpenseRepository {
            return INSTANCE ?:
            throw IllegalStateException("ExpenseRepository must be initialized")
        }
    }
}