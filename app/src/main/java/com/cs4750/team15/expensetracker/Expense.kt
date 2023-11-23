package com.cs4750.team15.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Expense (
    @PrimaryKey val id: UUID,
    val title: String,
    val date: Date,
    val amount: Int,
    val category: String,
    val photoFileName: String? = null
)