package com.cs4750.team15.expensetracker.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cs4750.team15.expensetracker.databinding.MessageBinding
import java.util.UUID

class ChatHolder(val binding: MessageBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(message: ChatMessage) {
        if(message.user == "You"){
            binding.cardGchatMessageMe.setCardBackgroundColor(Color.BLUE)
        }
        else{
            binding.cardGchatMessageMe.setCardBackgroundColor(Color.GRAY)
        }
        binding.textGchatMessageMe.text = message.msgContent
    }
}

class ChatListAdapter(
    private var messages: List<ChatMessage>,
):  RecyclerView.Adapter<ChatHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MessageBinding.inflate(inflater, parent, false)
        return ChatHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val message = messages[position]
        //holder.bind(expense, onExpenseClicked)
        holder.apply {
            binding.textGchatMessageMe.text = message.msgContent
        }
    }

    override fun getItemCount() = messages.size
    fun addMessage(message: ChatMessage){
        messages += message
    }
}