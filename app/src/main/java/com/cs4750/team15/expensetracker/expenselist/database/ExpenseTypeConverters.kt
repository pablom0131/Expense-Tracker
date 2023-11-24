package com.cs4750.team15.expensetracker.expenselist.database

import androidx.room.TypeConverter
import java.util.Date

class ExpenseTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}