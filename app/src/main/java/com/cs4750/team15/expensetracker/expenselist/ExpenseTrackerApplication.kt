package com.cs4750.team15.expensetracker.expenselist

import android.app.Application

class ExpenseTrackerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        ExpenseRepository.initialize(this)
    }
}