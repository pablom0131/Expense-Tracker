package com.cs4750.team15.expensetracker

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_screen)

        val expenseListButton = findViewById<Button>(R.id.expense_list_button)
        val spendingAnalysisButton = findViewById<Button>(R.id.spending_analysis_button)
        val chatButton = findViewById<Button>(R.id.chat_button)

        expenseListButton.setOnClickListener {
            setContentView(R.layout.expense_list_main)
        }
    }
}