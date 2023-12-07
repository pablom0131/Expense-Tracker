package com.cs4750.team15.expensetracker.chat

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GptApi {
    @GET("PennyWiseChat")
    suspend fun getChatResponse(@Query("message") msg : String?) : String
}